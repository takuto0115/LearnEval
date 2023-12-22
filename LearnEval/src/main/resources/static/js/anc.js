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

$(function() {
 
  $('button#add').click(function(){
 
  var tr_form = '' +
  '<tr>'+
      '<td><input type="text" name="text_1[]"></td>'+
      '<td><input type="text" name="text_2[]"></td>'+
    '</tr>'+
    '<tr>'+
      '<th>C</th>'+
      '<th>D</th>'+
    '</tr>'+
    '<tr>'+
      '<td><input type="text" name="text_3[]"></td>'+
      '<td><input type="text" name="text_4[]"></td>'+
    '</tr>';
 
  $(tr_form).appendTo($('table > tbody'));
 
});
 
 
});