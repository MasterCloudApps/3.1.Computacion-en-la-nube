const mustacheExpress = require('mustache-express');
const express = require('express');
const port = 5000;

let app = express()

const images = [
    "https://media.giphy.com/media/mlvseq9yvZhba/giphy.gif",
    "https://media.giphy.com/media/MCfhrrNN1goH6/giphy.gif",
    "https://media.giphy.com/media/8vQSQ3cNXuDGo/giphy.gif",
    "https://media.giphy.com/media/rwCX06Y5XpbLG/giphy.gif",
    "https://media.giphy.com/media/vFKqnCdLPNOKc/giphy.gif",
    "https://media.giphy.com/media/CqVNwrLt9KEDK/giphy.gif",
    "https://media.giphy.com/media/MWSRkVoNaC30A/giphy.gif",
    "https://media.giphy.com/media/TEkr9oBZ57KFmMWScZ/giphy.gif"
]

// Register '.html' extension with The Mustache Express
app.engine('html', mustacheExpress());
app.set('view engine', 'html');
app.set('views', __dirname + '/views');

app.get('/', function (req, res) {
    let image = images[Math.floor(Math.random() * images.length)];
    res.render('index', { url: image });
});

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`)
})