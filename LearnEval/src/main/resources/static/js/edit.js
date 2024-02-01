/**
 * 
 */
function home(){
    var result = window.confirm('編集中ですがホームに戻りますか？\n(編集内容は破棄されます)');

    if( result ) {
		window.location.href = "/teachermain";
    }
}

//function edit(num){
//	var  num = document.getElementById(num);
//    var result = window.confirm('設問'+num+'を編集しますか？');
//
//    if(! result ) {
//		window.location.href = "/testedit/"+num;
//    }
//}
//
//function insert(){
//	var num = document.getElementById(num);
//	var result = window.confirm('設問を追加しますか？');
//
//	if (!result) {
//		window.location.href = "/testedit/" + num
//}
//}

    function toggleSection(sectionId) {
      var section = document.getElementById(sectionId);
      if (section.style.display == "block") {
        section.style.display = "none";
      } else {
        section.style.display = "block";
      }
    }