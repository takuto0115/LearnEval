/**
 * 
 */  
$(document).ready(function()
{
	$(".radio_select li").click(function(obj)
	{
		// ラジオボタンにチェックを入れる
		$(this).children("input[type=radio]").prop("checked", true);
 
		// 枠の背景をいったん消す
		$(this).parent("ul").children("li").removeClass("select");
 
		// クリックした枠の背景に色を付ける
		$(this).addClass("select");
	});
});

    $(document).ready(function(){
      // ページ読み込み時に実行される処理
      // ラジオボタンの初期状態をチェックし、"select" クラスを適用する
      $('input[type=radio][name=type]').each(function() {
        if ($(this).is(':checked')) {
          $(this).parent().addClass('select');
        }
      });
    });