document.onkeydown = checkKey;
document.onkeyup = stopRobot;

var baseUrl = "http://192.168.0.106:8080/command?cmdname="
var isCommandSent = false

var HttpClient = function() {
    this.get = function(aUrl, aCallback) {
        var anHttpRequest = new XMLHttpRequest();
        anHttpRequest.onreadystatechange = function() {
            if (anHttpRequest.readyState == 4 && anHttpRequest.status == 200)
                aCallback(anHttpRequest.responseText);
        }

        anHttpRequest.open( "GET", aUrl, true );
        anHttpRequest.send( null );
    }
}

function checkKey(e) {
    if (!isCommandSent){
        isCommandSent = true

        e = e || window.event;
        if (e.keyCode == '38') {
            // up arrow
            var commandStr = baseUrl.concat("forward");
            httpGet(commandStr)
        } else if (e.keyCode == '40') {
            // down arrow
            var commandStr = baseUrl.concat("reverse");
            httpGet(commandStr)
        } else if (e.keyCode == '37') {
            // left arrow
            var commandStr = baseUrl.concat("left");
            httpGet(commandStr)
        } else if (e.keyCode == '39') {
            // right arrow
            var commandStr = baseUrl.concat("right");
            httpGet(commandStr)
        }
    }
}

function stopRobot(e) {
    var commandStr = baseUrl.concat("stop");
    httpGet(commandStr)
    isCommandSent = false
}

function httpGet(theUrl) {
    console.log(theUrl);
    var client = new HttpClient();
    client.get(theUrl, function(response) {
        console.log(response);
    });
}