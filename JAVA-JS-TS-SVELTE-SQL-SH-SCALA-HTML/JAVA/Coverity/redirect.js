const express = require('express');
const app = express();

const redirects = [
    {
        from: "/gestorepec/loggedout",
        to: "https://business.poste.it"
    },
    {
        from: "/loggedout",
        to: "https://business.poste.it"
    },
    {
        from: "/gestorepec/loggedout/",
        to: "https://business.poste.it"
    },
    {
        from: "/loggedout/",
        to: "https://business.poste.it"
    }
];

app.use((req, res, next) => {
    const redirect = redirects.find((r) => req.url.startsWith(r.from));

    if (redirect) {
        return res.redirect(301, redirect.to);
    }

    next();
});

module.exports = app;