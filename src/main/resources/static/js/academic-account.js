document.querySelectorAll('#create-account').forEach(toggle => {
    const fields = document.getElementById('account-fields');
    const update = () => {
        fields.hidden = !toggle.checked;
        fields.querySelectorAll('input').forEach(input => {
            input.disabled = !toggle.checked;
            input.required = toggle.checked;
        });
    };
    toggle.addEventListener('change', update);
    update();
});
