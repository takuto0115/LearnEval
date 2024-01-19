window.onload = function() {
            var scrollContainer = document.getElementById('messagearea');
            var lastScrollPosition = localStorage.getItem('lastScrollPosition');

            if (lastScrollPosition !== null) {
                scrollContainer.scrollTop = parseInt(lastScrollPosition);
            } else {
                scrollContainer.scrollTop = scrollContainer.scrollHeight;
            }

            scrollContainer.addEventListener('scroll', function() {
                localStorage.setItem('lastScrollPosition', scrollContainer.scrollTop);
            });

            // Retrieve and set input field value
            var messageInput = document.getElementById('messageInput');
            var savedMessage = localStorage.getItem('savedMessage');
            if (savedMessage !== null) {
                messageInput.value = savedMessage;
            }

            // Save input field value on change
            messageInput.addEventListener('input', function() {
                localStorage.setItem('savedMessage', messageInput.value);
            });

            // Clear input field value on form submission
            var messageForm = document.querySelector('form');
            messageForm.addEventListener('submit', function() {
                localStorage.removeItem('savedMessage');
            });

            // Automatic page refresh every 5 seconds
            setInterval(function() {
                location.reload();
            }, 5000);
        };