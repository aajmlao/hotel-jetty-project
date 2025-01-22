//load when the page is load
document.addEventListener('DOMContentLoaded', () => {
    const body = document.querySelector("body");
    const username = body.getAttribute('loginUser');
    // get the login time
    function loginTime() {
        const loginTime = document.getElementById("loginTime");
        fetch(`/api/loginTime?username=${username}`,{
            method: "GET",
            headers: {"Content-Type": "application/json"},
        }).then(response => response.json())
            .then(data => {
                if (data.username === username)  {
                    if (data.time !== "Null" && data.time !== "Null") {
                        loginTime.textContent = `Last Login: ${data.time} ${data.date}`;
                    } else {
                        loginTime.textContent = "You have not logged in before";
                    }
                }
            }).catch(error => console.log(error));
    }
    //call the function
    loginTime();
    //display the expedia from api request
    function displayExpedia() {
        const expediaDiv = document.getElementById("expediaLink");
        expediaDiv.innerHTML = "";
        const ul = document.createElement("ul");
        console.log(expediaDiv);
        fetch(`/api/expediaLink?username=${username}`,{
            method: "GET",
            headers: {"Content-Type": "application/json"},
        }).then(response => response.json())
        .then(data => {
            if (data.numLink === "NotNull") {
                data.expedia.forEach(expediaLink => {
                    const li = document.createElement("li");
                    li.innerHTML = `
                            <div class="d-flex gap-3 align-items-center">
                                <a href="${expediaLink.link}" class="link-offset-2 link-underline link-underline-opacity-0">Expedia hotel ID: ${expediaLink.hotelId} </a>
                                <button type="button" class="btn btn-link" id="deleteEX">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash" viewBox="0 0 16 16">
                                        <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z"/>
                                        <path d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z"/>
                                    </svg>
                                </button>
                            </div>`;
                    ul.appendChild(li);
                    const deleteEX = li.querySelector("#deleteEX");
                    deleteEX.addEventListener("click", (e) => {
                        e.preventDefault();
                        deleteExpedia(username, expediaLink.hotelId);
                    })
                })
                expediaDiv.appendChild(ul);
            } else {
                expediaDiv.innerHTML = `<h3>No Expedia</h3>`;
            }
        })
    }
    // call the method
    displayExpedia();


    // send delete request to backend in java
    function deleteExpedia(username, hotelId) {
        fetch(`/api/expediaLink`,{
            method: "DELETE",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({username, hotelId}),
        }).then(response => {
            return response.text();
        })
            .then(data => {
                console.log("Link is deleted", data)
                displayExpedia();
            }).catch(error => console.log(error));
    }
})