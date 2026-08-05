requireAuth();

document.getElementById("logoutLink").addEventListener("click", (e) => {
  e.preventDefault();
  clearSession();
  window.location.href = "index.html";
});

const DAYS = ["SATURDAY", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY"];
// These must match the class times set up on the backend (PeriodSchedule.java)
const PERIOD_LABELS = {
  1: "8:00 AM - 9:20 AM",
  2: "9:30 AM - 10:50 AM",
  3: "11:00 AM - 12:20 PM",
  4: "12:30 PM - 1:50 PM",
  5: "2:00 PM - 3:20 PM",
  6: "3:30 PM - 4:50 PM",
  7: "5:00 PM - 6:20 PM"
};
const PERIODS = [1, 2, 3, 4, 5, 6, 7];

async function loadFriendCheckboxes() {
  const wrapper = document.getElementById("friendCheckboxes");
  try {
    const friends = await apiRequest("/friends", "GET");

    if (friends.length === 0) {
      wrapper.innerHTML = "<p>Add some friends first on the Friends page.</p>";
      document.getElementById("findSlotsBtn").disabled = true;
      return;
    }

    let html = "";
    friends.forEach(f => {
      html += `
        <label style="display:inline-block; margin: 6px 14px 6px 0;">
          <input type="checkbox" class="friend-checkbox" value="${f.friendId}"> ${f.name} (${f.studentId})
        </label>`;
    });
    wrapper.innerHTML = html;
  } catch (err) {
    wrapper.textContent = "Could not load friends: " + err.message;
  }
}

document.getElementById("findSlotsBtn").addEventListener("click", async () => {
  const checkboxes = document.querySelectorAll(".friend-checkbox:checked");
  const friendIds = Array.from(checkboxes).map(cb => cb.value);

  const resultsWrapper = document.getElementById("resultsWrapper");
  resultsWrapper.textContent = "Finding common free slots...";

  try {
    const query = friendIds.length > 0 ? "?friendIds=" + friendIds.join(",") : "";
    const freeSlots = await apiRequest("/free-slot" + query, "GET");
    renderResults(freeSlots);
  } catch (err) {
    resultsWrapper.textContent = "Error: " + err.message;
  }
});

function renderResults(freeSlots) {
  const resultsWrapper = document.getElementById("resultsWrapper");

  if (freeSlots.length === 0) {
    resultsWrapper.innerHTML = "<p>No common free slots found for the selected group.</p>";
    return;
  }

  // Build a quick lookup so we can render it like a timetable
  const lookup = {};
  DAYS.forEach(d => { lookup[d] = {}; });
  freeSlots.forEach(s => { lookup[s.day][s.period] = true; });

  let html = "<table><thead><tr><th>Time</th>";
  DAYS.forEach(d => { html += `<th>${d.charAt(0) + d.slice(1).toLowerCase()}</th>`; });
  html += "</tr></thead><tbody>";

  PERIODS.forEach(p => {
    html += `<tr><td><strong>${PERIOD_LABELS[p]}</strong></td>`;
    DAYS.forEach(d => {
      html += lookup[d][p] ? `<td class="free">Free</td>` : `<td class="busy">-</td>`;
    });
    html += "</tr>";
  });

  html += "</tbody></table>";
  resultsWrapper.innerHTML = html;
}

loadFriendCheckboxes();
