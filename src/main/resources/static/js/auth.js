document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const toast = document.getElementById('toast');
    const submitBtn = document.getElementById('submitBtn');

    // Utility to show messages
    const showToast = (message, isError = true) => {
        toast.textContent = message;
        toast.className = `toast ${isError ? 'error' : 'success'}`;
        toast.scrollIntoView({ behavior: 'smooth' });
    };

    // Toggle button spinner
    const setSubmitting = (isSubmitting) => {
        const textSpan = submitBtn.querySelector('.btn-text');
        const spinner = submitBtn.querySelector('.spinner');
        if (isSubmitting) {
            submitBtn.disabled = true;
            spinner.classList.remove('hidden');
            textSpan.classList.add('hidden');
        } else {
            submitBtn.disabled = false;
            spinner.classList.add('hidden');
            textSpan.classList.remove('hidden');
        }
    };

    // Login Form Handler
    if (loginForm) {
        // Clear session check or check if already authenticated
        fetch('/api/auth/me')
            .then(res => {
                if (res.ok) window.location.href = '/dashboard.html';
            })
            .catch(() => {});

        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const usernameOrEmail = document.getElementById('usernameOrEmail').value.trim();
            const password = document.getElementById('password').value;

            if (!usernameOrEmail || !password) {
                showToast("All fields are required.");
                return;
            }

            setSubmitting(true);
            toast.classList.add('hidden');

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ usernameOrEmail, password })
                });

                const data = await response.json();
                
                if (response.ok) {
                    showToast("Login successful! Redirecting...", false);
                    setTimeout(() => {
                        window.location.href = '/dashboard.html';
                    }, 800);
                } else {
                    showToast(data.message || "Invalid credentials.");
                }
            } catch (err) {
                showToast("Connection error. Please try again.");
            } finally {
                setSubmitting(false);
            }
        });
    }

    // Register Form Handler
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const fullName = document.getElementById('fullName').value.trim();
            const username = document.getElementById('username').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (!fullName || !username || !email || !password || !confirmPassword) {
                showToast("All fields are required.");
                return;
            }

            if (password !== confirmPassword) {
                showToast("Passwords do not match.");
                return;
            }

            if (password.length < 6) {
                showToast("Password must be at least 6 characters.");
                return;
            }

            setSubmitting(true);
            toast.classList.add('hidden');

            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        fullName,
                        username,
                        email,
                        password,
                        confirmPassword
                    })
                });

                const data = await response.json();

                if (response.ok) {
                    showToast("Registration successful! Redirecting to sign in...", false);
                    setTimeout(() => {
                        window.location.href = '/login.html';
                    }, 1500);
                } else {
                    showToast(data.message || "Registration failed.");
                }
            } catch (err) {
                showToast("Connection error. Please try again.");
            } finally {
                setSubmitting(false);
            }
        });
    }
});
