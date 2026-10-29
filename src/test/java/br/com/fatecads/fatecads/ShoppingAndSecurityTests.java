package br.com.fatecads.fatecads;

import br.com.fatecads.fatecads.entity.*;
import br.com.fatecads.fatecads.repository.*;
import br.com.fatecads.fatecads.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ShoppingAndSecurityTests {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired ProductRepository products;
    @Autowired OrderRepository orders;
    @Autowired ItemOfOrderRepository items;
    @Autowired UserService userService;
    @Autowired ProductService productService;
    @Autowired CheckoutService checkout;
    @Autowired ItemOfOrderService itemService;
    @Autowired PasswordEncoder encoder;
    @Autowired CourseRepository courses;
    @Autowired ProfessorRepository professors;
    @Autowired StudentRepository students;
    @Autowired SubjectRepository subjects;
    @Autowired PasswordResetService resets;

    Product product;
    @BeforeEach void setup() {
        User buyer = new User(); buyer.setFullName("Test Buyer"); buyer.setEmail("buyer@test.local");
        buyer.setLogin("buyer"); buyer.setPassword("Buyer!Password123"); userService.save(buyer);
        product = new Product(); product.setProductDescription("Academic notebook"); product.setProductPrice(0.10);
        product.setProductBrand("Fatec"); product.setProductUnit("unit"); products.save(product);
    }

    @Test void rootCanAuthenticateAndIsProtected() throws Exception {
        var root = users.findByLogin("root").orElseThrow();
        assertThat(root.getRole()).isEqualTo("ROLE_ADMIN");
        assertThat(encoder.matches("TestRoot!Password123", root.getPassword())).isTrue();
        mvc.perform(formLogin("/login").user("root").password("TestRoot!Password123"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/home"));
        assertThatThrownBy(() -> userService.deleteById(root.getUserId())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test void publicSignupCannotEscalateOrOverwriteAnotherAccount() throws Exception {
        int rootId = users.findByLogin("root").orElseThrow().getUserId();
        mvc.perform(post("/users/save").with(csrf()).param("userId", "" + rootId)
                .param("fullName", "New Buyer").param("login", "newbuyer").param("email", "new@test.local")
                .param("password", "NewBuyer!Password123").param("role", "ROLE_ADMIN")
                .param("resetToken", "attacker"))
                .andExpect(redirectedUrl("/login?registered"));
        var registered = users.findByLogin("newbuyer").orElseThrow();
        assertThat(registered.getRole()).isEqualTo("ROLE_USER");
        assertThat(registered.getResetToken()).isNull();
        assertThat(users.findById(rootId).orElseThrow().getLogin()).isEqualTo("root");
    }

    @Test void csrfAndRoleRulesProtectEveryAdministrativeArea() throws Exception {
        mvc.perform(get("/cart")).andExpect(status().is3xxRedirection());
        mvc.perform(post("/cart/add").with(user("buyer")).param("productId", "" + product.getProductId()))
                .andExpect(status().isForbidden());
        for (String path : new String[]{"/users/list", "/student/list", "/course/list", "/subject/list", "/professor/list",
                "/order/list", "/order/create", "/item-of-order/list", "/product/new", "/product/edit/" + product.getProductId()}) {
            mvc.perform(get(path).with(user("buyer"))).andExpect(status().isForbidden());
        }
        mvc.perform(post("/product/delete/" + product.getProductId()).with(user("buyer")).with(csrf()))
                .andExpect(status().isForbidden());
        mvc.perform(get("/product/delete/" + product.getProductId()).with(user("root").roles("ADMIN")))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test void navigationAndAllExistingPagesRenderForTheirRoles() throws Exception {
        for (String path : new String[]{"/fatecads", "/login", "/users/create", "/forgot-password", "/reset-password?token=test"})
            mvc.perform(get(path)).andExpect(status().isOk());
        mvc.perform(get("/home").with(user("buyer"))).andExpect(status().isOk())
                .andExpect(content().string(containsString("Explore the catalog")))
                .andExpect(content().string(not(containsString("Manage users"))));
        mvc.perform(get("/product/list").with(user("buyer"))).andExpect(status().isOk())
                .andExpect(content().string(containsString("Add to cart")))
                .andExpect(content().string(not(containsString("New product"))));
        for (String path : new String[]{"/home", "/users/list", "/users/create", "/student/list", "/student/new",
                "/course/list", "/course/new", "/subject/list", "/subject/new", "/professor/list", "/professor/new",
                "/product/list", "/product/new", "/order/list", "/order/create", "/item-of-order/list", "/item-of-order/new"})
            mvc.perform(get(path).with(user("root").roles("ADMIN"))).andExpect(status().isOk());
        mvc.perform(get("/users/edit/" + users.findByLogin("root").orElseThrow().getUserId()).with(user("root").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(content().string(not(containsString("$2a$"))));
    }

    @Test void cartSupportsAddMergeUpdateRemoveAndClearWithSessionIsolation() throws Exception {
        MockHttpSession session = new MockHttpSession();
        for (int i = 0; i < 2; i++)
            mvc.perform(post("/cart/add").session(session).with(user("buyer")).with(csrf())
                    .param("productId", "" + product.getProductId()).param("quantity", "2")).andExpect(redirectedUrl("/cart"));
        mvc.perform(get("/cart").session(session).with(user("buyer"))).andExpect(status().isOk())
                .andExpect(model().attribute("total", new java.math.BigDecimal("0.40")));
        mvc.perform(get("/cart").with(user("other"))).andExpect(model().attribute("total", java.math.BigDecimal.ZERO));
        mvc.perform(post("/cart/update").session(session).with(user("buyer")).with(csrf())
                .param("productId", "" + product.getProductId()).param("quantity", "3")).andExpect(redirectedUrl("/cart"));
        mvc.perform(get("/cart").session(session).with(user("buyer"))).andExpect(model().attribute("total", new java.math.BigDecimal("0.30")));
        mvc.perform(post("/cart/remove").session(session).with(user("buyer")).with(csrf())
                .param("productId", "" + product.getProductId())).andExpect(redirectedUrl("/cart"));
        mvc.perform(get("/cart").session(session).with(user("buyer"))).andExpect(model().attribute("canCheckout", false));
        mvc.perform(post("/cart/add").session(session).with(user("buyer")).with(csrf()).param("productId", "" + product.getProductId()));
        mvc.perform(post("/cart/clear").session(session).with(user("buyer")).with(csrf())).andExpect(redirectedUrl("/cart"));
        mvc.perform(get("/cart").session(session).with(user("buyer"))).andExpect(model().attribute("canCheckout", false));
    }

    @Test void checkoutUsesCurrentServerPricesAndOnlyOwnerCanReadReceipt() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mvc.perform(post("/cart/add").session(session).with(user("buyer")).with(csrf())
                .param("productId", "" + product.getProductId()).param("quantity", "3"));
        product.setProductPrice(2.15); products.saveAndFlush(product);
        mvc.perform(post("/cart/checkout").session(session).with(user("buyer")).with(csrf())
                .param("total", "0.01")).andExpect(redirectedUrl("/purchases"));
        var saved = orders.findByUserLoginOrderByOrderIdDesc("buyer").get(0);
        assertThat(saved.getOrderTotal()).isEqualTo(6.45);
        assertThat(saved.getItem()).hasSize(1);
        mvc.perform(get("/purchases/" + saved.getOrderId()).with(user("buyer"))).andExpect(status().isOk())
                .andExpect(content().string(containsString("6.45")));
        mvc.perform(get("/purchases/" + saved.getOrderId()).with(user("other"))).andExpect(status().isNotFound());
        mvc.perform(get("/purchases").with(user("other"))).andExpect(status().isOk())
                .andExpect(content().string(containsString("No orders yet")));
        mvc.perform(get("/cart").session(session).with(user("buyer"))).andExpect(model().attribute("canCheckout", false));
        mvc.perform(post("/cart/checkout").session(session).with(user("buyer")).with(csrf()))
                .andExpect(redirectedUrl("/cart"));
        assertThat(orders.findByUserLoginOrderByOrderIdDesc("buyer")).hasSize(1);
        assertThatThrownBy(() -> itemService.deleteById(saved.getItem().get(0).getItemId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void invalidAndUnavailableCartItemsCannotBeOrdered() throws Exception {
        MockHttpSession session = new MockHttpSession();
        for (String quantity : new String[]{"0", "-1", "1000"})
            mvc.perform(post("/cart/add").session(session).with(user("buyer")).with(csrf())
                    .param("productId", "" + product.getProductId()).param("quantity", quantity))
                    .andExpect(flash().attributeExists("errorMessage"));
        mvc.perform(post("/cart/add").session(session).with(user("buyer")).with(csrf())
                .param("productId", "" + product.getProductId()).param("quantity", "1.5"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/cart/add").session(session).with(user("buyer")).with(csrf()).param("productId", "" + product.getProductId()));
        products.delete(product); products.flush();
        mvc.perform(get("/cart").session(session).with(user("buyer"))).andExpect(status().isOk())
                .andExpect(model().attribute("canCheckout", false));
        mvc.perform(post("/cart/checkout").session(session).with(user("buyer")).with(csrf()))
                .andExpect(redirectedUrl("/cart")).andExpect(flash().attributeExists("errorMessage"));
        assertThat(orders.findByUserLoginOrderByOrderIdDesc("buyer")).isEmpty();
    }

    @Test void userEditingKeepsPasswordAndRejectsDuplicatesAndWeakPasswords() throws Exception {
        var buyer = users.findByLogin("buyer").orElseThrow();
        String hash = buyer.getPassword();
        mvc.perform(post("/users/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("userId", "" + buyer.getUserId()).param("fullName", "Updated Buyer")
                .param("login", "buyer").param("email", "buyer@test.local").param("password", "").param("role", "ROLE_USER"))
                .andExpect(redirectedUrl("/users/list"));
        assertThat(users.findByLogin("buyer").orElseThrow().getPassword()).isEqualTo(hash);
        mvc.perform(post("/users/save").with(csrf()).param("fullName", "Duplicate")
                .param("login", "buyer").param("email", "unique@test.local").param("password", "LongPassword123!"))
                .andExpect(view().name("user/userForm")).andExpect(model().attributeExists("errorMessage"));
        mvc.perform(post("/users/save").with(csrf()).param("fullName", "Weak Password")
                .param("login", "weak").param("email", "weak@test.local").param("password", "short"))
                .andExpect(view().name("user/userForm")).andExpect(model().attributeExists("errorMessage"));
    }

    @Test void uploadsRejectDisguisedHtmlAndPreserveExistingImage() throws Exception {
        product.setImagePath("/uploads/existing.png"); products.saveAndFlush(product);
        Product edit = new Product(); edit.setProductId(product.getProductId()); edit.setProductDescription("Edited");
        edit.setProductBrand("Fatec"); edit.setProductUnit("unit"); edit.setProductPrice(1.50);
        edit.setImagePath("https://untrusted.example/track");
        assertThat(productService.save(edit, null).getImagePath()).isEqualTo("/uploads/existing.png");
        assertThatThrownBy(() -> productService.save(edit, new MockMultipartFile("image", "evil.png", "image/png", "<script>alert(1)</script>".getBytes())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void academicCrudAndLegacyOrdersWorkTogether() throws Exception {
        mvc.perform(post("/course/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("courseName", "Computer Science").param("period", "Evening").param("workloadHours", "2400"))
                .andExpect(redirectedUrl("/course/list"));
        Course course = courses.findAll().get(0);
        mvc.perform(post("/professor/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("fullName", "Test Professor").param("phone", "(11) 99999-9999")
                .param("graduation", "Computer Science").param("RM", "RM123"))
                .andExpect(redirectedUrl("/professor/list"));
        Professor professor = professors.findAll().get(0);
        assertThat(professor.getPhone()).isEqualTo("11999999999");
        mvc.perform(post("/student/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("fullName", "Test Student").param("email", "student@test.local")
                .param("phone", "(11) 98888-8888").param("address", "Campus street, 1")
                .param("cpf", "123.456.789-01").param("registrationNumber", "RA123")
                .param("course", "" + course.getCourseId())).andExpect(redirectedUrl("/student/list"));
        Student student = students.findAll().get(0);
        assertThat(student.getCpf()).isEqualTo("12345678901");
        mvc.perform(post("/subject/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("subjectName", "DevOps").param("code", "DEV101").param("workloadHours", "80")
                .param("course", "" + course.getCourseId()).param("professor", "" + professor.getProfessorId()))
                .andExpect(redirectedUrl("/subject/list"));
        Subject subject = subjects.findAll().get(0);
        for (String path : new String[]{"/course/edit/" + course.getCourseId(), "/professor/edit/" + professor.getProfessorId(),
                "/student/edit/" + student.getStudentId(), "/subject/edit/" + subject.getSubjectId(),
                "/product/edit/" + product.getProductId()})
            mvc.perform(get(path).with(user("root").roles("ADMIN"))).andExpect(status().isOk());
        String payload = "{\"student\":{\"studentId\":" + student.getStudentId()
                + "},\"orderTotal\":0.01,\"item\":[{\"product\":{\"productId\":" + product.getProductId()
                + "},\"amount\":3,\"price\":0.01}]}";
        mvc.perform(post("/order").with(user("root").roles("ADMIN")).with(csrf())
                .contentType("application/json").content(payload)).andExpect(status().isOk());
        Order order = orders.findAll().get(0);
        assertThat(order.getOrderTotal()).isEqualTo(0.30);
        mvc.perform(get("/order/" + order.getOrderId()).with(user("root").roles("ADMIN"))).andExpect(status().isOk());
        ItemOfOrder item = order.getItem().get(0);
        mvc.perform(post("/item-of-order/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("itemId", "" + item.getItemId()).param("order", "" + order.getOrderId())
                .param("product", "" + product.getProductId()).param("amount", "5").param("price", "0.01"))
                .andExpect(redirectedUrl("/item-of-order/list"));
        assertThat(orders.findById(order.getOrderId()).orElseThrow().getOrderTotal()).isEqualTo(0.50);
        mvc.perform(post("/item-of-order/delete/" + item.getItemId()).with(user("root").roles("ADMIN")).with(csrf()))
                .andExpect(redirectedUrl("/item-of-order/list"));
        assertThat(orders.findById(order.getOrderId()).orElseThrow().getOrderTotal()).isEqualTo(0.0);
        // Delete dependents first, matching the actual foreign-key relationships.
        orders.delete(order); orders.flush();
        mvc.perform(post("/subject/delete/" + subject.getSubjectId()).with(user("root").roles("ADMIN")).with(csrf()))
                .andExpect(redirectedUrl("/subject/list"));
        mvc.perform(post("/student/delete/" + student.getStudentId()).with(user("root").roles("ADMIN")).with(csrf()))
                .andExpect(redirectedUrl("/student/list"));
        mvc.perform(post("/professor/delete/" + professor.getProfessorId()).with(user("root").roles("ADMIN")).with(csrf()))
                .andExpect(redirectedUrl("/professor/list"));
        mvc.perform(post("/course/delete/" + course.getCourseId()).with(user("root").roles("ADMIN")).with(csrf()))
                .andExpect(redirectedUrl("/course/list"));
    }

    @Test void passwordRecoveryDoesNotExposeAccountsAndTokensAreSingleUse() throws Exception {
        mvc.perform(post("/forgot-password").with(csrf()).param("email", "missing@test.local"))
                .andExpect(status().isOk()).andExpect(model().attributeExists("successMessage"));
        User buyer = users.findByLogin("buyer").orElseThrow();
        String token = "test-secret-token";
        String hash = java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(token.getBytes()));
        buyer.setResetToken(hash); buyer.setResetTokenExpiry(java.time.LocalDateTime.now().plusMinutes(30)); users.save(buyer);
        assertThat(resets.resetPassword(token, "weak")).isFalse();
        assertThat(resets.resetPassword(token, "Replacement!Password123")).isTrue();
        assertThat(encoder.matches("Replacement!Password123", buyer.getPassword())).isTrue();
        assertThat(resets.resetPassword(token, "Replacement!Password123")).isFalse();
        buyer.setResetToken(hash); buyer.setResetTokenExpiry(java.time.LocalDateTime.now().minusMinutes(1)); users.save(buyer);
        assertThat(resets.resetPassword(token, "Replacement!Password123")).isFalse();
    }

    @Test void invalidAcademicAndOrderDataAreRejected() throws Exception {
        mvc.perform(post("/course/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("courseName", "Invalid course").param("period", "Evening").param("workloadHours", "-1"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/order").with(user("root").roles("ADMIN")).with(csrf())
                .contentType("application/json").content("{\"item\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test void productMultipartFormAcceptsRealPngAndShowsErrorsForInvalidImages() throws Exception {
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(new java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_RGB), "png", bytes);
        mvc.perform(multipart("/product/save").file(new MockMultipartFile("image", "sample.png", "image/png", bytes.toByteArray()))
                .with(user("root").roles("ADMIN")).with(csrf()).param("productDescription", "Uploaded product")
                .param("productPrice", "12.34").param("productUnit", "unit").param("productBrand", "Fatec"))
                .andExpect(redirectedUrl("/product/list"));
        Product uploaded = products.findAll().stream().filter(p -> p.getProductDescription().equals("Uploaded product")).findFirst().orElseThrow();
        assertThat(uploaded.getImagePath()).startsWith("/uploads/").endsWith(".png");
        mvc.perform(multipart("/product/save").file(new MockMultipartFile("image", "fake.png", "image/png", "<script>bad</script>".getBytes()))
                .with(user("root").roles("ADMIN")).with(csrf()).param("productDescription", "Invalid upload")
                .param("productPrice", "12.34").param("productUnit", "unit").param("productBrand", "Fatec"))
                .andExpect(view().name("product/productForm")).andExpect(model().attributeExists("errorMessage"));
    }
}
