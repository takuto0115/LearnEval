/**
 * 
 */
function A() {
        $(".B").toggleClass("C");
}

function home(){
    var result = window.confirm('編集中ですがホームに戻りますか？\n(編集内容は破棄されます)');

    if( result ) {
		window.location.href = "/teachermain";
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