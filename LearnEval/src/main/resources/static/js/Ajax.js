//$(document).ready(function () {
//    /* messageReloadへPOSTしする */
//    function addNewMessage() {
//        $.ajax({
//            type: "POST",
//            url: "/messageReload",
//        })
//            .done(function (data) {
//                /* 新規メッセージを追加したHTMLをID:new_messageに追加していく */
//                $("#newMessage").append(data);
//            })
//    }
//    /* 5秒ごとにイベントを発生させる */
//    $(function () {
//        setInterval(function () {
//            addNewMessage();
//        }, 5000);
//    });
//}
