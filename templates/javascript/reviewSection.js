document.addEventListener("DOMContentLoaded", function (event) {
    const body = document.querySelector("body");
    const hotelId = body.getAttribute("dataHotelId");
    const loginUser = body.getAttribute("loginUser");
    const expediaLink = body.getAttribute("expediaLink");
    // console.log(hotelId)
    // console.log(loginUser)
    const page = 1;
    const listSize = 5;

    /**
     * when the expedia is click send request to back and insert the data
     */
    function queryExpedia() {
        const expediaA = document.getElementById("expedia");
        expediaA.addEventListener("click", function () {
            fetch(`/api/expediaLink`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({expediaLink, hotelId}),
            }).then(response =>{
                return response.json();
            })
                .then(data => {
                    console.log("data is sent : " + data);
                }).catch(error => console.log("Error : " + error));
        })
    }

    queryExpedia();
    // use to pop up the add review form
    const addReview = document.getElementById("addRevBtn");
    const addReviewDiv = document.getElementById("addReviewDiv");
    addReview.addEventListener("click", function () {
        addReviewDiv.innerHTML = ` 
                     <div class="modal fade" id="addReviewModal" tabindex="-1" aria-labelledby="addReviewModalLabel" >
                        <div class="modal-dialog modal-dialog-centered" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="addReviewModalLabel">Add a Review</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                   <form id="reviewForm">
                                        <div class="mb-3">
                                            <label for="title" class="form-label">Title: </label><br>
                                            <input type="text" id="title" name="title" class="form-control" required/><br><br>
                                        </div>
                                        <div class="mb-3">
                                            <label for="text" class="form-label">Review: </label><br>
                                            <textarea id="text" name="text" class="form-control" required></textarea><br><br>
                                        </div>
                                        <div class="mb-3">
                                            <label for="rating" class="form-label">Rating(1-5):</label>
                                            <input type="number" id="rating" name="rating" min="1" max="5" class="form-control" required><br><br>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="cancelAddReview" data-bs-dismiss="modal">Cancel</button>
                                            <button type="submit" form="reviewForm" class="btn btn-success" data-bs-dismiss="modal">Submit Review</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                    `;

        const modal = document.getElementById("addReviewModal");
        const addReviewModel = new bootstrap.Modal(modal);
        addReviewModel.show();
        // call the function and send the data to add
        addReviewFunc(hotelId, page, listSize);
    })

    /**
     * list the reviews.
     * @param query
     * @param page
     * @param listSize
     */
    function displayReviews(query, page, listSize) {
        fetch(`/api/reviews?hotelId=${query}&page=${page}&listSize=${listSize}`, {
            method:"GET",
            headers: {"Content-Type": "application/json"},
        })
            .then(response => response.json())
            .then(data => {
                console.log(data)

                const totalCount = data.totalCount;
                const rating = data.totalRating / totalCount;
                const totalPages = Math.ceil(totalCount / listSize);
                const displayRating = document.getElementById("avgRating")

                const reviewDiv = document.getElementById("reviewDiv");
                reviewDiv.innerHTML = " ";
                reviewDiv.innerHTML = `<h3 class="modal-title">Review: </h3>`
                const ul = document.createElement("ul");
                ul.className = "list-group";
                data.reviews.forEach(review => {
                    const li = document.createElement("li");
                    if (`${review.hasReview}` === "true") {
                        li.innerHTML = `<h5>${review.title}</h5>
                                         <p>Username: ${review.userName}/Rating: ${review.rating}</p>
                                         <p>Review: ${review.text}</p>
                                         
                                        `;
                        displayRating.textContent = `Average: ${rating.toFixed(2)}`;
                    } else {
                        li.innerHTML = `<h3>No Review</h3>`
                        displayRating.textContent = "No Rating";
                    }
                    li.className = "list-group-item";
                    ul.appendChild(li);
                    reviewDiv.appendChild(ul);
                    // append edit button
                    if (review.userName === loginUser) {
                        console.log("EDIT Review");
                        const editButton = document.createElement("button");
                        const deleteButton = document.createElement("button")
                        editButton.textContent = "Edit";
                        editButton.className = "reviewButton btn btn-primary btn-sm";
                        editButton.setAttribute("data-toggle", "modal")
                        editButton.setAttribute("data-target", "#editReviewModal");
                        editButton.style.marginRight = "7px";
                        deleteButton.textContent = "Delete Review";
                        deleteButton.className = "btn btn-danger btn-sm";
                        editButton.addEventListener("click", function() {editReview(review, li, query, page, listSize)});
                        deleteButton.addEventListener("click", function () {deleteReview(review.reviewId, query, page, listSize)});
                        li.appendChild(editButton);
                        li.appendChild(deleteButton);
                    }
                    if (review.userName !== loginUser) {
                        console.log("add review under review");
                        const addCommentButton = document.createElement("button");
                        const displayCommentButton = document.createElement("button");
                        displayCommentButton.textContent = "show comment";
                        displayCommentButton.className = "btn btn-info";
                        displayCommentButton.setAttribute("style", "--bs-btn-padding-y: .2rem; --bs-btn-padding-x: .3rem; --bs-btn-font-size: .65rem");
                        addCommentButton.textContent = "Add Comment";
                        addCommentButton.className = "btn btn-primary";
                        addCommentButton.setAttribute("style", "--bs-btn-padding-y: .2rem; --bs-btn-padding-x: .3rem; --bs-btn-font-size: .65rem");
                        li.appendChild(addCommentButton);
                        li.appendChild(displayCommentButton)

                        const commentContainer = document.createElement("div");
                        commentContainer.className = "commentContainer";
                        li.appendChild(commentContainer);

                        addCommentButton.addEventListener("click", function () {
                            addComment(review, li, commentContainer);
                        })

                        displayCommentButton.addEventListener("click", function () {
                            console.log("show comment");
                            displayComment(review.reviewId, commentContainer);
                        })
                    }
                })
                displayPagination(query, totalPages, page, listSize);

            }).catch(error => console.log("Error : " + error))
    }

    function addComment(review, li, commentContainer) {
        const commentForm = document.createElement("div");
        commentForm.className = "commentDiv";
        commentForm.innerHTML = `<div class="modal fade" id="addCommentModal" tabindex="-1" aria-labelledby="addCommentModalLabel" >
                        <div class="modal-dialog modal-dialog-centered" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="addCommentModalLabel">Add a Comment</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                   <form id="commentForm">
                                        <div class="mb-3">
                                            <label for="com" class="form-label">Comment: </label><br>
                                            <textarea id="com" name="comment" class="form-control" required></textarea><br><br>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="cancelAddComment btn btn-dark" data-bs-dismiss="modal">Cancel</button>
                                            <button type="submit" form="commentForm" class="btn btn-success" data-bs-dismiss="modal">Submit Review</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                    `;

        li.appendChild(commentForm);
        const modal = document.getElementById("addCommentModal");
        const addCommentModel = new bootstrap.Modal(modal);
        addCommentModel.show();
        const reviewId = `${review.reviewId}`;
        commentForm.addEventListener("submit", (e) => {
            e.preventDefault();
            const text = commentForm.querySelector("#com").value;
            fetch(`/api/comments`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({text, reviewId, hotelId, loginUser}),
            }).then(response => {
                return response.text();
            }).then(data => {
                console.log("Success adding comment", data.text);
                displayComment(reviewId, commentContainer);
            })
        })

    }

    function displayComment(reviewId, commentContainer) {
        commentContainer.innerHTML = "";
        commentContainer.className = "border list-group";
        const commentUL = document.createElement("ul");
        fetch(`/api/comments?hotelId=${hotelId}&reviewId=${reviewId}&username=${loginUser}`, {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        }).then(response => response.json())
            .then(data => {
                data.comments.forEach(comment => {
                    const commentDiv = document.createElement("li");
                    commentDiv.innerHTML = `
                        <p class="ms-4">Comment by ${comment.username}:  ${comment.text}</p>
                    `;
                    commentDiv.className = "list-group-item";
                    commentUL.appendChild(commentDiv);
                    commentContainer.appendChild(commentUL);
                })
            })
    }
    /**
     * use to send request to delete
     * @param reviewId
     * @param query
     * @param page
     * @param listSize
     */
    function deleteReview(reviewId, query, page, listSize) {
        const deleteButton = confirm("Are you want to delete this review?");
        if (deleteButton) {
            alert("Review will remove.");
        } else {
            return;
        }
        fetch(`/api/reviews`, {
            method:"DELETE",
            headers: {"Content-Type": "application/json"},
            body:JSON.stringify({reviewId})
            })
            .then(response => {
                if (!response.ok) throw new Error("Fail to delete review")
                return response.text();
            })
            .then(data => {
                console.log("Review is deleted", data)
                displayReviews(query, page, listSize);
            }).catch(error => console.log("Error : " + error))
    }

    /**
     * modify the review data.
     * @param review
     * @param div
     * @param query
     * @param page
     * @param listSize
     */
    function editReview(review, div, query, page, listSize) {
        const originalContent = div.innerHTML;
        const editDiv = document.createElement("div");
        editDiv.innerHTML = `
            <div class="modal fade" id="editReviewModal" tabindex="-1" aria-labelledby="editReviewModalLabel">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="editReviewModalLabel">Modify a Review</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="editForm">
                                <div class="mb-3">
                                    <label for="editTitle">Title: </label><br>
                                    <input type="text" id="editTitle" name="editTitle" value="${review.title}" class="form-control" required/><br><br>
                                </div>
                                <div class="mb-3">
                                    <label for="editText">Review: </label><br>
                                    <textarea id="editText" name="editText" class="form-control" required>${review.text}</textarea><br><br>
                                </div>
                                <div class="mb-3">
                                    <label for="editRate" >Review(1-5): </label><br>
                                    <input type="number" id="editRate" name="editRate" min="1" max="5" value="${review.rating}" class="form-control" required/><br>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="cancelButton" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" id="submitChange" class="btn btn-success" data-bs-dismiss="modal">Save Changes</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
    `;
        div.appendChild(editDiv);
        const editModal = document.getElementById("editReviewModal");
        const editReviewModel = new bootstrap.Modal(editModal);
        editReviewModel.show();

        editDiv.querySelector("#editForm").addEventListener("submit", function (event) {
            event.preventDefault();

            const reviewId = `${review.reviewId}`;
            const updateTitle = document.getElementById("editTitle").value;
            const updateText = document.getElementById("editText").value;
            const updateRate = document.getElementById("editRate").value;

            fetch(`/api/reviews`, {
                method: "PUT",
                headers:{"Content-Type": "application/json"},
                body: JSON.stringify({updateTitle, updateText, updateRate:parseFloat(updateRate), reviewId})
            })
                .then(response => {
                    if (!response.ok) throw new Error("Fail to add review")
                    return response.text(); //did not work with json will look up later
                })
                .then(data => {
                    console.log("Review updated:", data);
                    displayReviews(query, page, listSize);
                }).catch(error => console.log("Error : " + error))
        })

        const cancelEditButton = editDiv.querySelector("#editForm").querySelector("button.cancelButton");
        cancelEditButton.className = "btn btn-dark";
        cancelEditButton.addEventListener("click", function (event) {
            event.preventDefault()

            div.innerHTML = originalContent;
            const editButton = div.querySelector("button.reviewButton")
            if (editButton) {
                editButton.addEventListener("click", function () {displayReviews(query, page, listSize);})
            }
        })
    }

    /**
     * send review request to the backend java in ReviewsServlet
     * @param query
     * @param page
     * @param listSize
     */
    function addReviewFunc(query, page, listSize) {
        const reviewFunc = document.getElementById("reviewForm");
        reviewFunc.addEventListener("submit", function (event){
            event.preventDefault();

            const title = document.getElementById("title").value;
            const text = document.getElementById("text").value;
            const rating = document.getElementById("rating").value;

            fetch(`/api/reviews`, {
                method:"POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({title, text , rating: parseFloat(rating), hotelId})
            })
                .then(response => {
                    if (!response.ok) throw new Error("Fail to add review")
                    return response.text();
                })
                .then(data => {
                    console.log("Review added:", data);
                    displayReviews(query, 1, listSize);
                }).catch(error => console.log("Error : " + error))
        })

        const cancelBtn = document.querySelector("button.cancelAddReview");
        cancelBtn.className = "btn btn-dark"
        cancelBtn.addEventListener("click", function () {
            reviewFunc.innerHTML = ""
        })
    }

    function displayPagination(query, totalPages, currentPage, listSize) {
        const pagination = document.getElementById("reviewBtn");
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
                displayReviews(query, i, listSize);
            })
            pagination.appendChild(pageBtn);
        }

    }

    displayReviews(hotelId, page, listSize);

    /**
     * favor or like the hotels.
     */
    function star() {
        const noFillStar = `
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-star" viewBox="0 0 16 16">
            <path d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.56.56 0 0 0-.163-.505L1.71 6.745l4.052-.576a.53.53 0 0 0 .393-.288L8 2.223l1.847 3.658a.53.53 0 0 0 .393.288l4.052.575-2.906 2.77a.56.56 0 0 0-.163.506l.694 3.957-3.686-1.894a.5.5 0 0 0-.461 0z"/>
        </svg>`;
        const fillStar = `
         <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-star-fill" viewBox="0 0 16 16">
            <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"/>
        </svg>`;
        const favorBtn = document.getElementById("starBTN");
        console.log(hotelId + " " + loginUser);
        fetch(`/api/user?hotelId=${hotelId}&username=${loginUser}`, {
            method:"GET",
            headers: {"Content-Type": "application/json"},
        }).then(response => response.json())
            .then(data => {
                // console.log(loginUser + " "+ data.username)
                if (data.isFavor && (loginUser === data.username)) {
                    favorBtn.innerHTML = fillStar;
                } else {
                    favorBtn.innerHTML = noFillStar;
                }
            }).catch(error => console.log("Error : " + error));

        favorBtn.addEventListener("click", function () {
            const isFill = favorBtn.querySelector(".bi-star-fill") !== null;
            if (isFill) {
                fetch(`/api/user`, {
                    method:"DELETE",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({hotelId})
                }).then(response => {
                    return response.text();
                })
                    .then(data => {
                        console.log("Removed: "+data);
                        favorBtn.innerHTML = noFillStar;
                    }).catch(error => console.log("Error : " + error));
            } else {
                fetch(`/api/user`, {
                    method:"POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({hotelId})
                }).then(response => {
                    return response.text();
                }).then(data => {
                    console.log("Added: "+data);
                    favorBtn.innerHTML = fillStar;
                }).catch(error => console.log("Error : " + error));
            }

        })
    }

    star();

})

