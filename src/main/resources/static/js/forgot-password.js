// Runs on forgot-password.html

const forgotPasswordForm = document.getElementById("forgotPasswordForm");
if (forgotPasswordForm) {
  forgotPasswordForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const errorMsg = document.getElementById("errorMsg");
    const successMsg = document.getElementById("successMsg");
    errorMsg.textContent = "";
    successMsg.textContent = "";

    const email = document.getElementById("email").value.trim();
    const studentId = document.getElementById("studentId").value.trim();
    const newPassword = document.getElementById("newPassword").value;

    try {
      const data = await apiRequest("/forgot-password", "POST", { email, studentId, newPassword });
      successMsg.textContent = data.message || "Password updated. You can now log in.";
      forgotPasswordForm.reset();
      setTimeout(() => {
        window.location.href = "index.html";
      }, 1500);
    } catch (err) {
      errorMsg.textContent = err.message;
    }
  });
}