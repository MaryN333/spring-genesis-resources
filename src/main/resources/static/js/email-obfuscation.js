//document.addEventListener('DOMContentLoaded', function() {
//    var emails = document.querySelectorAll('.email-obfuscate');
//    emails.forEach(function(email) {
//        var user = email.dataset.user;
//        var domain = email.dataset.domain;
//        var at = '&#64;'; // HTML-сущность для @
//        var dot = '&#46;'; // HTML-сущность для .
//        var fullEmail = user + at + domain;
//        email.innerHTML = '<a href="mailto:' + user + '@' + domain + '">' + fullEmail + '</a>';
//    });
//});

document.addEventListener('DOMContentLoaded', function() {
    var emails = document.querySelectorAll('.email-obfuscate');
    emails.forEach(function(email) {
        var parts = [
            email.dataset.p1, // первая часть имени
            email.dataset.p2, // вторая часть имени
            email.dataset.d1, // первая часть домена
            email.dataset.d2  // вторая часть домена
        ];

        var user = parts[0] + parts[1];
        var domain = parts[2] + '.' + parts[3];

        email.innerHTML = '<a href="mailto:' + user + '@' + domain + '">' + user + '&#64;' + domain + '</a>';
    });
});