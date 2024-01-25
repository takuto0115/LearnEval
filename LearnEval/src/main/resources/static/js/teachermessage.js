document.addEventListener('DOMContentLoaded', function() {
    var scrollContainer = document.getElementById('messagearea');
    var messageInput = document.getElementById('messageInput');

    // localStorageから最後のスクロール位置を取得
    var lastScrollPosition = localStorage.getItem('lastScrollPosition');
    // ストアされた値に基づいてスクロール位置を設定
    if (lastScrollPosition !== null) {
        scrollContainer.scrollTop = parseInt(lastScrollPosition, 10);
    } else {
        scrollContainer.scrollTop = scrollContainer.scrollHeight;
    }

    // ユーザーがスクロールしたときにスクロール位置をlocalStorageに保存
    scrollContainer.addEventListener('scroll', function() {
        localStorage.setItem('lastScrollPosition', scrollContainer.scrollTop);
    });

    // localStorageから保存されたメッセージを取得し、入力値として設定
    var savedMessage = localStorage.getItem('savedMessage');
    if (savedMessage !== null) {
        messageInput.value = savedMessage;
    }

    // 入力値が変更されたときにlocalStorageにメッセージを保存
    messageInput.addEventListener('input', function() {
        localStorage.setItem('savedMessage', messageInput.value);
    });

    // メッセージエリアを更新する関数
    function updateMessages() {
        // roomID要素から値を取得
        var roomIDElement = document.getElementById('roomID');
        var roomID = roomIDElement ? roomIDElement.textContent : '';

        // roomIDが取得できない場合は、ここで処理を終了する
        if (!roomID) {
            console.error('roomIDが見つかりません。');
            return;
        }

        // Fetch APIを使用してサーバーからデータを取得
        fetch("/teachermessage/" + roomID)
            .then(function(response) {
                return response.text();
            })
            .then(function(html) {
                var parser = new DOMParser();
                var doc = parser.parseFromString(html, 'text/html');
                var newMessageArea = doc.getElementById('messagearea').innerHTML;

                // messageareaのHTMLを更新
                if (scrollContainer.innerHTML !== newMessageArea) {
                    var shouldScroll = scrollContainer.scrollTop + scrollContainer.clientHeight === scrollContainer.scrollHeight;
                    scrollContainer.innerHTML = newMessageArea;

                    // 更新後、自動的にスクロールするか判断
                    if (shouldScroll) {
                        scrollContainer.scrollTop = scrollContainer.scrollHeight;
                    }
                }

                // 更新後のスクロール位置をlocalStorageに保存
                localStorage.setItem('lastScrollPosition', scrollContainer.scrollTop);
            })
            .catch(function(error) {
                console.error("メッセージの更新に失敗しました。", error);
            });
    }

    // 5秒ごとにメッセージエリアを更新
    setInterval(updateMessages, 5000);

    // フォームの送信を処理するためのコード
    var messageForm = document.querySelector('form');
    messageForm.addEventListener('submit', function(event) {
        // フォームが送信されたときに保存されたメッセージと入力値をクリア
        localStorage.removeItem('savedMessage');
        messageInput.value = '';
        // デフォルトのフォーム送信を行う
    });
});
