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

function testdelete(num){
    var result = window.confirm("問"+num+"を削除しますか？");
	
    if( result ) {
		location.href = "/deletetest/" + num;
	}
	else {
		alert("キャンセルされました");
	}
}

function questiondelete() {
		alert("削除しました。");
	}

function edit(message){
	alert(message + "が完了しました。");
}    
function newtest(){
	alert("新規テストを作成しました。\n編集画面へ移行します。");
}