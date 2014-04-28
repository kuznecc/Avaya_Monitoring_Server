/**
 * Created by ayudin on 15.02.14.
 */
function loadPage(target, urlToLoad) {
    $('#' + target).load(urlToLoad);
}

function replaceTagWithHtml(target, urlToLoad) {
    $.get(urlToLoad, function(data) {
        $("#"+target).replaceWith(data);
    });
}

function getCurrentDate() {
    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth() + 1; //January is 0!

    var yyyy = today.getFullYear();
    if (dd < 10) {
        dd = '0' + dd
    }
    if (mm < 10) {
        mm = '0' + mm
    }
    today = mm + '/' + dd + '/' + yyyy;
    return today;
}

function disableButtonsInDivWithText(divId, buttonsText) {
    $("#" + divId).find(":button").each(
        function () {
            if ($(this).text() === buttonsText) {
                $(this).attr("disabled", "disabled");
            }
        }
    );
}

function enableButtonsInDivWithText(divId, buttonsText) {
    $("#" + divId).find(":button").each(
        function () {
            if ($(this).text() === buttonsText) {
                $(this).removeAttr("disabled");
            }
        }
    );
}