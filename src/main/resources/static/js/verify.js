addEventListener('keypress', () => {
    if (errorMessage.style.display !== "none") {
        errorMessage.style.display = "none";
    }
})

const verifyForm = document.getElementById("verifyForm");
const errorMessage = document.getElementById("errorMessage");

verifyForm.addEventListener('submit', (event) => {
    event.preventDefault();

    const code = document.getElementById('code').value;

    const data = {code};
    const jsonData = JSON.stringify(data);

    fetch('/verify', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: jsonData
    }).then(response => {
        if (response.ok) {
            window.location.href = '/';
        } else {
            response.text().then(text => {
                errorMessage.textContent = text || "Something went wrong. Please try again.";
                errorMessage.style.display = "block";
            });
        }
    }).catch(error => {
        errorMessage.textContent = "An error occurred: " + error.message;
        errorMessage.style.display = "block";
    });
});
