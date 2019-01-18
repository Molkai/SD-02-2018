var MongoClient = require('mongodb').MongoClient;
var mongourl = "mongodb://localhost:27017/";
var maqs
var provedorId = 1;
var http = require('http')
var https = require('https')
var url = require('url')
var disponivel
var flag = true

var AtualizaCloudBroker = () => {
    MongoClient.connect(mongourl, function(err, db) {
      if (err) throw err;
      let dbo = db.db("Provedores-DB");

      // Maquinas provedor 1
      dbo.collection("maquinas"+(provedorId)).find({}, { projection: { _id: 0 } }).toArray(function(err, result) {
        if (err) throw err;

    	let data = {}
        data.op = true
        data.provedorId = provedorId
    	data.table = []
        if(flag == true)
            disponivel = new Array(result.length)
    	result.forEach(function(maq){
    		'use strict';
            if(flag == true)
                disponivel[maq.idM] = true;

    		let maquina = {
    			idmaquina: maq.idM,
    			qtd_vCPU: maq.qtd_vCPU,
    			RAM: maq.RAM,
    			HD: maq.HD,
    			preco_hora: maq.preco_hora,
                disponivel: disponivel[maq.idM],
    		};
    		data.table.push(maquina)

    	});
        flag = false

    	maqs = JSON.stringify(data, null, 2);

        https.get('https://cloudbroker-sd.herokuapp.com/provedor?request='+JSON.stringify(data), (resp) => {
          let resposta = '';

          // A chunk of data has been recieved.
          resp.on('data', (chunk) => {
            resposta += chunk;
          });

          // The whole response has been received. Print out the result.
          resp.on('end', () => {
            console.log(JSON.parse(resposta).mensagem);
          });

        });
        db.close();
      });
    });
}

const provedor = () => {
    AtualizaCloudBroker();

    var server = http.createServer(function(req, res){
        console.log("Requisição feita: " + req.url);
        res.writeHead(200, {'Content-Type': 'application/json'});

        let receivedUrl = url.parse(req.url, true)
        let resposta = {}
        if(receivedUrl.pathname == '/cliente'){
            let operation = JSON.parse(receivedUrl.query.request)
            if(operation.op == true){
                disponivel[operation.maquina] = false
                resposta.mensagem = 'Recurso de id ' + operation.maquina + ' ja pode ser utilizado.'
            }
            else {
                disponivel[operation.maquina] = true
                AtualizaCloudBroker();
                resposta.mensagem = 'Recurso de id ' + operation.maquina + ' liberado com sucesso.'
            }
        }
        else
            resposta.mensagem = 'Apenas clientes podem pedir recursos.'

        resp = JSON.stringify(resposta);
        res.end(resp);
    });

    server.listen(10000+provedorId, 'localhost', () => {
        console.log('Provedor rodando em http://localhost:'+(10000+provedorId))
    })
}

process.on('SIGINT', () => {

    let data = {
        provedorId: provedorId,
        op: false,
    };

    https.get('https://cloudbroker-sd.herokuapp.com/provedor?request='+JSON.stringify(data), (res) => {
        let resp=''

        res.on('data', (chunck) => {
            resp+=chunck
        })

        res.on('end', () => {
            console.log('\n'+JSON.parse(resp).mensagem)
            process.exit();
        })
    })
})

provedor();
