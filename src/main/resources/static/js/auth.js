// Runs on index.html (login) and signup.html

const loginForm = document.getElementById("loginForm");
if (loginForm) {
  loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const errorMsg = document.getElementById("errorMsg");
    errorMsg.textContent = "";

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    try {
      const data = await apiRequest("/login", "POST", { email, password });
      setSession(data.token, { id: data.userId, name: data.name, email: data.email });
      window.location.href = "dashboard.html";
    } catch (err) {
      errorMsg.textContent = err.message;
    }
  });
}

const signupForm = document.getElementById("signupForm");
if (signupForm) {
  signupForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const errorMsg = document.getElementById("errorMsg");
    errorMsg.textContent = "";

    const studentId = document.getElementById("studentId").value.trim();
    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const semester = document.getElementById("semester").value.trim();
    const password = document.getElementById("password").value;

    try {
      const data = await apiRequest("/signup", "POST", { studentId, name, email, semester, password });
      setSession(data.token, { id: data.userId, name: data.name, email: data.email });
      window.location.href = "dashboard.html";
    } catch (err) {
      errorMsg.textContent = err.message;
    }
  });
}
