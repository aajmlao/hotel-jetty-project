/**
 * Load when page is load
 */
document.addEventListener("DOMContentLoaded", function (event) {
    event.preventDefault();
    const body = document.querySelector("body");
    const loginUname = body.getAttribute("loginUser");
    const query = document.getElementById("hotelQuery").value;
    const page = 1;
    const listSize = 10;
    fetchHotels(query, page, listSize);

    document.getElementById("hotelSearchForm").addEventListener("submit", function (event) {
        event.preventDefault();
        const query = document.getElementById("hotelQuery").value;
        const page = 1;
        const listSize = 10;
        console.log("Test");
        fetchHotels(query, page, listSize);
    });

    /**
     * fetch Hotels data and update the page as ajax
     * @param query
     * @param page
     * @param listSize
     */
    function fetchHotels(query, page, listSize) {
        fetch(`/login/hotels?hotelQuery=${query}&page=${page}&listSize=${listSize}`, {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        })
            .then(response => response.json())
            .then(data => {
                const hotels = data.hotels;
                const totalCount = data.totalCount;
                displayHotelList(hotels, query);
                const totalPages = Math.ceil(totalCount / listSize);
                displayPagination(query, totalPages, page, listSize);
            }).catch(error => console.log("Error : " + error));

    }

    /**
     * display HotelList
     * @param hotels
     * @param query
     */
    function displayHotelList(hotels, query) {
        const hotelList = document.getElementById("hotelList");
        hotelList.innerHTML = "";
        if (hotels.length === 0) {
            console.log(`No hotels found with ${query}`);
            hotelList.innerHTML = `<p>No hotels found with query: ${query}</p>`;
            return;
        }

        const ol = document.createElement("ol");
        ol.className = "list-group list-group-numbered";

        hotels.forEach(hotel => {
            const a = document.createElement("a");
            a.href = `/login/hotel/info?hotelId=${hotel.hotelId}`;
            a.className = "link-offset-2 link-offset-3-hover link-underline link-underline-opacity-0 link-underline-opacity-75-hover";
            const li = document.createElement("li");
            li.className = "list-group-item";
            fetch(`/api/user?hotelId=${hotel.hotelId}&username=${loginUname}`, {
                method: "GET",
                headers: {"Content-Type": "application/json"},
            }).then(response => response.json())
                .then(data => {
                    // console.log(data.username + " "+ loginUname);
                    if (data.isFavor && (data.username === loginUname)) {
                        li.innerHTML = `
                            ${hotel.hotelName}
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-star-fill" viewBox="0 0 16 16">
                                <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"/>
                            </svg>
                        `;
                    } else {
                        li.innerHTML = `${hotel.hotelName}`;
                    }
                }).catch(error => console.log("Error : " + error));

            a.appendChild(li);
            ol.appendChild(a);
        })
        hotelList.appendChild(ol);
    }

    /**
     * display the page button and send the request to the api update the page
     * @param query
     * @param totalPages
     * @param currentPage
     * @param listSize
     */
    function displayPagination(query, totalPages, currentPage, listSize) {
        const pagination = document.getElementById("pagination");
        pagination.innerHTML = "";

        if (totalPages <= 1) {
            return;
        }
        for (let i = 1; i <= totalPages; i++) {
            const pageBtn = document.createElement("button");
            pageBtn.textContent = i;
            pageBtn.className = "btn btn-primary";
            pageBtn.style.margin = "4px";
            pageBtn.addEventListener("click", function () {
                fetchHotels(query, i, listSize);
            })
            pagination.appendChild(pageBtn);
        }

    }
})

