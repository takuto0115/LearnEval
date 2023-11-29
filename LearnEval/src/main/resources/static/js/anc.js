/**
 * 
 */
$(function() {
    $(".A").click(function() {
        $(".B").toggleClass("C");
    });
});

function home(){
    var result = window.confirm('編集中ですがホームに戻りますか？\n(編集は破棄されます)');
    
    if( result ) {
		window.location.href = "teachermain";
    }
}