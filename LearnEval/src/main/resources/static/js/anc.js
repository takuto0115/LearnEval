/**
 * 
 */
$(function() {
    $(".A").click(function() {
        $(".B").toggleClass("C");
    });
});

function home(){
    var result = window.confirm('編集中ですがホームに戻りますか？\n(編集内容は破棄されます)');

    if( result ) {
		window.location.href = "teachermain";
    }
}

//$(function() {
// 
//  $("#add").click(function(){
// 
//  var tr_form = '' +
//      '<tr>'+
//      '<th>A</th>'+
//      '<th>B</th>'+
//    '</tr>'+
//  '<tr>'+
//      '<td><input type="text" name="1"></td>'+
//      '<td><input type="text" name="2"></td>'+
//    '</tr>'+
//    '<tr>'+
//      '<th>C</th>'+
//      '<th>D</th>'+
//    '</tr>'+
//    '<tr>'+
//      '<td><input type="text" name="3"></td>'+
//      '<td><input type="text" name="4"></td>'+
//    '</tr>';
// 
//  $(tr_form).appendTo($('table > tbody'));
// 
//});
// 
// 
//});


function createInputFields() {
      // プルダウンで選択された値を取得
      var selectedQuantity = document.getElementById("quantity").value;

      // 入力フォームを表示するコンテナの参照を取得
      var inputContainer = document.getElementById("input-container");

      // コンテナ内の要素をクリア
      inputContainer.innerHTML = "";

      // 選択された数字分だけ入力フォームを生成してコンテナに追加
      
      var q = ['A','B','C','D'];
      
      for (var i = 0; i < selectedQuantity; i += 4) {
		  for(var k : q){
        var inputField = document.createElement("input");
        inputField.type = "text";
        inputField.name = i;
        }
        }
        
        inputContainer.appendChild(inputField);
      }
    }