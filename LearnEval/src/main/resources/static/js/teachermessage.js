document.addEventListener('DOMContentLoaded', function() {
    // メッセージエリアのコンテナを取得
    var scrollContainer = document.getElementById('messagearea');

    // localStorageから最後のスクロール位置を取得
    var lastScrollPosition = localStorage.getItem('lastScrollPosition');

    // ストアされた値に基づいてスクロール位置を設定または利用可能でない場合は一番下に設定
    if (lastScrollPosition !== null) {
        scrollContainer.scrollTop = parseInt(lastScrollPosition);
    } else {
        scrollContainer.scrollTop = scrollContainer.scrollHeight;
    }

    // ユーザーがスクロールしたときにスクロール位置をlocalStorageに保存
    scrollContainer.addEventListener('scroll', function() {
        localStorage.setItem('lastScrollPosition', scrollContainer.scrollTop);
    });

    // メッセージ入力エレメントを取得
    var messageInput = document.getElementById('messageInput');

    // localStorageから保存されたメッセージを取得し、入力値として設定
    var savedMessage = localStorage.getItem('savedMessage');
    if (savedMessage !== null) {
        messageInput.value = savedMessage;
    }

    // 入力値が変更されたときにlocalStorageにメッセージを保存
    messageInput.addEventListener('input', function() {
        localStorage.setItem('savedMessage', messageInput.value);
    });

//    // フォームの送信を処理
//    var messageForm = document.querySelector('form');
//    messageForm.addEventListener('submit', function(event) {
//        // フォームが送信されたときに保存されたメッセージと入力値をクリア
//        localStorage.removeItem('savedMessage');
//        messageInput.value = '';
//        event.preventDefault(); // デフォルトのフォーム送信を防ぐ
//    });

    // 5000ミリ秒（5秒）ごとにページをリロード
//	setInterval(function() {
//		location.reload();
//	}, 5000);

});
