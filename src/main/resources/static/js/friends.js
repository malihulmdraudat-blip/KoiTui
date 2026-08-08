requireAuth();

document.getElementById("logoutLink").addEventListener("click", (e) => {
  e.preventDefault();
  clearSession();
  window.location.href = "index.html";
});

document.getElementById("addFriendForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errorMsg = document.getElementById("errorMsg");
  errorMsg.textContent = "";

  const friendStudentId = document.getElementById("friendStudentId").value.trim();

  try {
    await apiRequest("/friends/add", "POST", { friendStudentId });
    document.getElementById("addFriendForm").reset();
    loadRequests();
  } catch (err) {
    errorMsg.textContent = err.message;
  }
});

async function removeFriend(friendId) {
  if (!confirm("Remove this friend?")) return;
  try {
    await apiRequest("/friends/remove/" + friendId, "DELETE");
    loadFriends();
  } catch (err) {
    alert(err.message);
  }
}

async function acceptRequest(requestId) {
  try {
    await apiRequest("/friends/requests/" + requestId + "/accept", "POST");
    loadRequests();
    loadFriends();
  } catch (err) {
    alert(err.message);
  }
}

async function rejectRequest(requestId) {
  try {
    await apiRequest("/friends/requests/" + requestId + "/reject", "POST");
    loadRequests();
  } catch (err) {
    alert(err.message);
  }
}

async function loadRequests() {
  const list = document.getElementById("requestsList");
  try {
    const requests = await apiRequest("/friends/requests", "GET");

    if (requests.length === 0) {
      list.innerHTML = "<p>No pending friend requests.</p>";
      return;
    }

    let html = "";
    requests.forEach(r => {
      html += `
        <div class="list-item">
          <div>
            <strong>${r.name}</strong>
            <span class="badge">${r.studentId}</span>
            ${r.semester ? `<span class="badge">${r.semester}</span>` : ""}
          </div>
          <div>
            <button class="btn btn-small" onclick="acceptRequest(${r.requestId})">Accept</button>
            <button class="btn btn-small btn-danger" onclick="rejectRequest(${r.requestId})">Reject</button>
          </div>
        </div>`;
    });
    list.innerHTML = html;
  } catch (err) {
    list.textContent = "Could not load friend requests: " + err.message;
  }
}

async function loadFriends() {
  const list = document.getElementById("friendsList");
  try {
    const friends = await apiRequest("/friends", "GET");

    if (friends.length === 0) {
      list.innerHTML = "<p>You haven't added any friends yet.</p>";
      return;
    }

    let html = "";
    friends.forEach(f => {
      html += `
        <div class="list-item">
          <div>
            <strong>${f.name}</strong>
            <span class="badge">${f.studentId}</span>
            ${f.semester ? `<span class="badge">${f.semester}</span>` : ""}
          </div>
          <button class="btn btn-small btn-danger" onclick="removeFriend(${f.friendId})">Remove</button>
        </div>`;
    });
    list.innerHTML = html;
  } catch (err) {
    list.textContent = "Could not load friends: " + err.message;
  }
}

loadRequests();
loadFriends();
