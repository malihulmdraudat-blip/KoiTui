requireAuth();

const user = getStoredUser();
document.getElementById("welcomeMsg").textContent = "Welcome, " + (user ? user.name : "Student") + "!";

document.getElementById("logoutLink").addEventListener("click", (e) => {
  e.preventDefault();
  clearSession();
  window.location.href = "index.html";
});

// Map JS day name to how the backend stores it
function getTodayName() {
  const days = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
  return days[new Date().getDay()];
}

async function loadDashboard() {
  try {
    const routine = await apiRequest("/routine", "GET");
    const today = getTodayName();
    const todayClasses = routine.filter(r => r.day === today);
    document.getElementById("todayCount").textContent = todayClasses.length;
  } catch (err) {
    document.getElementById("todayCount").textContent = "0";
  }

  try {
    const friends = await apiRequest("/friends", "GET");
    document.getElementById("friendCount").textContent = friends.length;

    if (friends.length > 0) {
      const friendIds = friends.map(f => f.friendId).join(",");
      const freeSlots = await apiRequest("/free-slot?friendIds=" + friendIds, "GET");
      const today = getTodayName();
      const todayFree = freeSlots.filter(s => s.day === today);
      document.getElementById("freeCount").textContent = todayFree.length;
    } else {
      document.getElementById("freeCount").textContent = "0";
    }
  } catch (err) {
    document.getElementById("friendCount").textContent = "0";
    document.getElementById("freeCount").textContent = "0";
  }
}

loadDashboard();
