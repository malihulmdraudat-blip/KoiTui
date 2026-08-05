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

document.getElementById("routineForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errorMsg = document.getElementById("errorMsg");
  errorMsg.textContent = "";

  const day = document.getElementById("day").value;
  const period = parseInt(document.getElementById("period").value, 10);
  const course = document.getElementById("course").value.trim();
  const room = document.getElementById("room").value.trim();

  try {
    await apiRequest("/routine", "POST", { day, period, course, room });
    document.getElementById("routineForm").reset();
    loadTimetable();
  } catch (err) {
    errorMsg.textContent = err.message;
  }
});

async function deleteEntry(id) {
  try {
    await apiRequest("/routine/" + id, "DELETE");
    loadTimetable();
  } catch (err) {
    alert(err.message);
  }
}

async function loadTimetable() {
  const wrapper = document.getElementById("timetableWrapper");
  let routine = [];

  try {
    routine = await apiRequest("/routine", "GET");
  } catch (err) {
    wrapper.textContent = "Could not load routine: " + err.message;
    return;
  }

  // Build a lookup: lookup[day][period] = entry
  const lookup = {};
  DAYS.forEach(d => { lookup[d] = {}; });
  routine.forEach(r => {
    if (lookup[r.day]) {
      lookup[r.day][r.period] = r;
    }
  });

  let html = "<table><thead><tr><th>Time</th>";
  DAYS.forEach(d => { html += `<th>${d.charAt(0) + d.slice(1).toLowerCase()}</th>`; });
  html += "</tr></thead><tbody>";

  PERIODS.forEach(p => {
    html += `<tr><td><strong>${PERIOD_LABELS[p]}</strong></td>`;
    DAYS.forEach(d => {
      const entry = lookup[d][p];
      if (entry) {
        html += `<td class="busy">${entry.course || "Class"}${entry.room ? "<br><small>" + entry.room + "</small>" : ""}<br>
                  <button class="btn btn-small btn-danger" style="margin-top:4px;" onclick="deleteEntry(${entry.id})">Remove</button></td>`;
      } else {
        html += `<td class="free">Free</td>`;
      }
    });
    html += "</tr>";
  });

  html += "</tbody></table>";
  wrapper.innerHTML = html;
}

loadTimetable();
