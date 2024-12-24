addEventListener('keypress', () => {
    if (errorMessage.style.display !== "none") {
        errorMessage.style.display = "none";
    }
})

const signupForm = document.getElementById("signupForm");
const errorMessage = document.getElementById("errorMessage");

signupForm.addEventListener('submit', (event) => {
    event.preventDefault();

    const firstname = document.getElementById('firstname').value;
    const lastname = document.getElementById('lastname').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('passwordcon').value;

    if (password !== confirmPassword) {
        errorMessage.textContent = "Passwords do not match!";
        errorMessage.style.display = "block";
        return;
    }

    const data = {firstname, lastname, email, password};
    const jsonData = JSON.stringify(data);

    fetch('/req/signup', {
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