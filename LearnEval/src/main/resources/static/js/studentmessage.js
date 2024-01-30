document.addEventListener('DOMContentLoaded', function() {
	//ページがロードされたときにmesssageareaのスクロール位置を一番下にする
    var scrollContainer = document.getElementById('messagearea');
    var messageInput = document.getElementById('messageInput');
    scrollContainer.scrollTop = scrollContainer.scrollHeight;

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
        fetch("/studentmessage/" + roomID)
            .then(function(response) {
                return response.text();
            })
            .then(function(html) {
                var parser = new DOMParser();
                var doc = parser.parseFromString(html, 'text/html');
                var newMessageArea = doc.getElementById('messagearea').innerHTML;

                // messageareaのHTMLを更新したことのフラグを立てる
                if (scrollContainer.innerHTML !== newMessageArea) {
                    scrollContainer.innerHTML = newMessageArea;
                    booleanScroll = true;
                    
                }
                // booleanScrollがtrueなら、スクロール位置を一番下にする
				if (booleanScroll) {
					scrollContainer.scrollTop = scrollContainer.scrollHeight;
					booleanScroll = false;
				}
                
                
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
        // フォームが送信されたときの処理
        // スクロール位置をいちばん下にする
        scrollContainer.scrollTop = scrollContainer.scrollHeight;
        
    });
});
