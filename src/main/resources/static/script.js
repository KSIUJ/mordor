
for (var elem of document.querySelectorAll(".clickable-row")) {
    (function(elem) {
        elem.addEventListener('click', function() {
            elem.querySelector('a').click();
        });
    })(elem);
}
