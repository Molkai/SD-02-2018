var http = require('http');
var url = require('url');

let provedores = new Array(4);
let provedoresLength = [0, 0, 0, 0];

let server = http.createServer((req, res) => {
    res.writeHead(200, {'Content-Type': 'application/json'});

    let receivedUrl = url.parse(req.url, true)
    let resposta = {}

    if(receivedUrl.pathname == '/provedor'){
        let operation = JSON.parse(receivedUrl.query.request)

        if(operation.op == true){
            let provedorId = operation.provedorId;
            provedores[provedorId] = operation.table;
            provedoresLength[provedorId] = operation.table.length;
            resposta.mensagem = "Atualização do provedor " + provedorId + " realizada com sucesso."
        } else {
            let provedorId = operation.provedorId;
            provedores[provedorId] = [];
            provedoresLength[provedorId] = 0;
            resposta.mensagem = "Provedor " + provedorId + " desligado com sucesso."
        }

    } else if(receivedUrl.pathname == '/cliente'){
        let search = JSON.parse(receivedUrl.query.request)

        if(search.op == true){
            let resource = search.resource;
            let minprice = -1;
            let melhormaquina = -1;
            let melhorprovedor = -1;
            let idmaquina = -1;
            let provedor;

            if(search.idProvedor == -1){
                for(let i = 0; i < 4; i ++){
                    provedor = provedores[i];
                    for(let j = 0; j < provedoresLength[i]; j++){
                        if(provedor[j].disponivel && provedor[j].qtd_vCPU == resource.qtd_vCPU && provedor[j].RAM == resource.RAM && provedor[j].HD == resource.HD && (provedor[j].preco_hora < minprice || minprice == -1)){
                            melhormaquina = j;
                            melhorprovedor = i;
                            idmaquina = provedor[j].idmaquina;
                            minprice = provedor[j].preco_hora;
                        }
                    }
                }
            } else {
                let i = search.idProvedor;
                provedor = provedores[i];
                for(let j = 0; j < provedoresLength[i]; j++){
                    if(provedor[j].disponivel && provedor[j].qtd_vCPU == resource.qtd_vCPU && provedor[j].RAM == resource.RAM && provedor[j].HD == resource.HD && (provedor[j].preco_hora < minprice || minprice == -1)){
                        melhormaquina = j;
                        melhorprovedor = i;
                        idmaquina = provedor[j].idmaquina;
                        minprice = provedor[j].preco_hora;
                    }
                }
            }

            if(melhorprovedor == -1){
                resposta.mensagem = "Nao existe uma maquina com as configuraçoes requisitadas";
            } else {
                provedor = provedores[melhorprovedor];
                provedor[melhormaquina].disponivel = false;
                resposta.mensagem = "Maquina encontrada com sucesso";
                resposta.provedor = melhorprovedor;
                resposta.maquina = idmaquina;
                resposta.price = minprice;
            }
        } else {
            resposta.mensagem = "Erro na opcao requisitada";
        }

    } else {
        resposta.mensagem = "Erro sobre quem esta requisitando algo";
    }

    resp = JSON.stringify(resposta);
    res.end(resp);
})

server.listen(5000, 'localhost', () => {
    console.log('Servidor rodando em http://localhost:5000')
})
