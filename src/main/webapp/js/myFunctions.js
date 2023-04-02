


let poll = (()=>{

    let pollData= {};

    pollData.getCookie = function (name) {
        return document.cookie.split('; ').reduce((r, v) => {
            const parts = v.split('=')
            return parts[0] === name ? decodeURIComponent(parts[1]) : r
        }, '')
    }
    //Set cookies of selected answer
    pollData.setCookie= function() {
        let now = new Date();
        let time = now.getTime();
        let expireTime = time + 1000*36000;
        now.setTime(expireTime);
        document.cookie = 'selectedAnswer=true;expires='+now.toUTCString()+';path=/';
    }

    pollData.createPoll = function(){
        fetch('/Hello-servlet')
            .then(pollData.status)
            .then(res => res.json())
            .then(result=> {
                document.getElementById("question").innerText += result;
            })
            .catch(function (){
                document.getElementById("question").innerText = "error";
            });
    }
    //submit handler
    pollData.enterSubmit = function() {
        let radios = document.getElementsByName('flexRadioDefault');
        for (let i = 0, length = radios.length; i < length; i++) {
            if (radios[i].checked) {
                poll.setCookie();
                pollData.postPollResult (radios[i].value);
                return;
            }
        }
        alert("please choose an answer");
    }

    pollData.printResultOfPoll = function(result) {
        document.getElementById("submitButton").hidden = true;
        document.getElementById("form").style.display = "none";
        document.getElementById("results").innerHTML = "<p>You have already voted, you can not vote twice</p></br><h2>The results of the poll are:</h2>"
        let indexAnswer;
        let arrOfAnswer = result.answers.split(",");
        let arrOfVotes =  result.votes.split(",");

        for (let index = 0; index <arrOfAnswer.length; index++) {
            indexAnswer=index+1;
            document.getElementById("results").innerHTML += "<h5>" +indexAnswer+". "+ arrOfAnswer[index] +": "+ arrOfVotes[index]+"</h5>";

        }
    }

    pollData.postPollResult = function(value) {
        fetch('/CreatePoll',  {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'datatype': 'json',

            },
            body: new URLSearchParams(value),
        }).then(res => res.json())
            .then(result=> {
                document.getElementById("form").style.display = "none";
                document.getElementById("results").innerHTML = "<h2>The results of the poll are:</h2>"
                for(const item in result)
                    document.getElementById("results").innerHTML += "<h5>" + item + ": " + result[item] + "</h5>";
            })
    }

    pollData.printPoll=function(result) {
        document.getElementById("question").innerHTML += "<h1>" + result.question + "</h1>";
        let arr = result.answers.split(",");
        for (let index = 0; index < arr.length; index++) {
            document.getElementById("answers").innerHTML += "<div class='form-check mt-2'>" +
                "<input class='form-check-input' type='radio' value=" + index + " name='flexRadioDefault' id='" +
                index + "'><label class='form-check-label' for='" + index + "'>" + arr[index] + "</label></div>";
        }
    }

    pollData.getHandler=function() {
        fetch('/CreatePoll')
            .then(res => res.json())
            .then(result => {
                if(pollData.getCookie("selectedAnswer")==="")
                    pollData.printPoll(result);
                else
                    pollData.printResultOfPoll (result) ;

            })
            .catch(function () {
                if (pollData.getCookie('error') === 'fileError')
                    document.getElementById("question").innerText = "The poll is not available now, please visit later";
                else
                    document.getElementById("question").innerText = 'error!!!';
                document.getElementById("submitButton").hidden = true;

            });
    }
    return pollData;
})();


document.addEventListener('DOMContentLoaded', function(){
    poll.getHandler();
    document.getElementById("submitButton").addEventListener('click', function () {
        poll.enterSubmit();
    });

});

