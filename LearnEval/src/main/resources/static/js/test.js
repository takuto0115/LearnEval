/**
 * 
 */
function home(message){
    var result = window.confirm(message+'中ですがホームに戻りますか？\n('+message+'内容は破棄されます)');
	
	console.log(message);
	console.log(result);
    if( result ) {
		if(message=="編集"){
			location.href = "/teachermain";
		} else if (message=="解答") {
			location.href = "/studentmain";
			}
	}
}

    function toggleSection(sectionId) {
      var section = document.getElementById(sectionId);
      if (section.style.display == "block") {
        section.style.display = "none";
      } else {
        section.style.display = "block";
      }
    }