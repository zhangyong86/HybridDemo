function loadingImg() {
    alert("欢迎使用native+h5");
}

//android中的toast显示信息框
function showToast(toast) {
    javascript: android.showToast(toast);
}
//警告  
function onAlert() {
    alert("This is a alert sample from html");
}
//确定  
function onConfirm() {
    //得到true/false
    var b = confirm("are you sure to login?");
    javascript: control.showToast("your choice is " + b);
}
//提示  
function onPrompt() {
    //得到输入内容  
    var b = prompt("please input your password", "aaa");
    javascript: control.showToast("your input is " + b);
}

//用于app向js反向输出日志
function info(msg) {
    console.info(msg);
}
