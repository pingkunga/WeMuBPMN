const jsonServer = require('json-server')
const server = jsonServer.create()
const router = jsonServer.router('db.json')
const middlewares = jsonServer.defaults()
const util = require('util')
const { parse } = require('querystring');

console.log("I am PingkungA")
// Set default middlewares (logger, static, cors and no-cache)
server.use(middlewares)

// Add custom routes before JSON Server router
server.get('/echo', (req, res) => {
  res.jsonp(req.query)
})

//Wait for chunked transfer
//https://stackoverflow.com/questions/40637975/writing-express-middleware-to-get-raw-request-body-before-body-parser
//custom middleware - req, res, next must be arguments on the top level function
/*
function handleChunkedReq(req, res, next) {
  req.rawBody = '';

  req.on('data', function(chunk) {
    req.rawBody += chunk;
    console.log(req.rawBody);
    
  });
  req.body = req.rawBody;
  // call next() outside of 'end' after setting 'data' handler
  next();  
}*/
// your middleware
//server.use(handleChunkedReq);
// To handle POST, PUT and PATCH you need to use a body-parser
// You can use the one used by JSON Server
server.use(jsonServer.bodyParser)
server.use((req, res, next) => {
  console.log("========================");
  //var data = '';
  if ((req.method === 'POST') || (req.method === 'PATCH') || (req.method === 'PUT')) {
    let body = [];
    req.on('data', function(chunk) {
      console.log("Received body data:");
      body.push(chunk);
    }).on('end', function() {
      //req.raw = Buffer.concat(body).toString();
      let data   = Buffer.concat(body);
      let schema = JSON.parse(data);
      req.raw = schema;
      console.log(req.raw);
      req.body = req.raw;
      console.log("Isarray : "+ Array.isArray(req.body))
      //console.log(util.inspect(req, {depth: null}));
      //req.body = data;
      //req.body = JSON.parse(data);
      if (req.method === 'POST') {
        req.body.createdAt = Date.now()
      }
      //Get Request Body
      console.log("req.body is " + req.body)
      console.log("req.raw is " + req.rawBody)
    });
  }
  // Continue to JSON Server router
  next() 
})

// Use default router
server.use(router)
server.listen(3000, () => {
  console.log('JSON Server is running')
})