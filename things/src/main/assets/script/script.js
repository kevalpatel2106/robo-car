console.log("robotWebUi.js : server IP= "+document.location.host);
var sock = new WebSocket('ws://' + document.location.host, "protocolOne");

sock.onopen = function (event) {
     console.log("I am connected to server.....");
    //document.getElementById("connection").value = 'CONNECTED';
};

sock.onmessage = function (event) {
    console.log("on message" + event.data);
    substring = "data:image/png;base64";
    if (event.data.indexOf(substring) !== -1){
        document.getElementById('IMG').setAttribute( 'src', event.data );
    }else{
         document.getElementById('TEXT').innerHTML = event.data;
    }
}

sock.onerror = function (error) {
    console.log('WebSocket Error',  error);
};

function send(message) {
    console.log('WebSocket try to send',  message);
    sock.send(message);
};

document.onkeydown = checkKey;
document.onkeyup = stopRobot;

var isCommandSent = false;
function checkKey(e) {
        if (!isCommandSent){
            isCommandSent = true;
            e = e || window.event;
            switch (event.keyCode || event.which) {
                case 87:
                case 38:
                     // up arrow
                    send("forward");
                    break;
                case 83:
                case 40:
                     // down arrow
                   send("reverse");
                    break;
                case 65:
                case 37:
                      // left arrow
                    send("left");
                    break;
                case 68:
                case 39:
                     send("right");
                    break;
            }
        }
}

function stopRobot(e) {
    send("stop");
    isCommandSent = false;
}