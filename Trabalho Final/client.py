import json
import requests
import sys
import signal

maquinas = []
provedorId = -1;

def signal_handler(sig, frame):
    print '\n'
    i = 0
    while i < len(maquinas):
        maquina = maquinas.pop(i)
        req = {
            "maquina": maquina["idmaquina"],
            "op": False
        }
        sendreq = {"request": json.dumps(req)}
        r = requests.get("http://localhost:"+str(10000+provedorId)+"/cliente", params=sendreq)
        print r.json()['mensagem']+'\n'
    sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)
opcao = int(raw_input("Opcao: "))

while True:
    if opcao == 1:
        print("Insira os dados para realizar uma consulta por maquina virtual: ")
        qtd_vCPU = int(raw_input("Insira a quantidade de vCPUs desejada: "))
        RAM = int(raw_input("Insira a quantidade de memoria RAM desejada (em GB): "))
        HD = int(raw_input("Insira a quantidade de memoria em disco (HD) desejada (em GB): "))

        resource = {
            "qtd_vCPU": qtd_vCPU,
            "RAM": RAM,
            "HD": HD
        }
        request = {
            "op": True,
            "idProvedor": provedorId,
            "resource": resource
        }

        consultaJSON = {"request": json.dumps(request)}

        r = requests.get("https://cloudbroker-sd.herokuapp.com/cliente", params=consultaJSON)

        if r.json()['mensagem'] == 'Maquina encontrada com sucesso':
            provedorId = r.json()['provedor']
            resource["idmaquina"] = r.json()['maquina']
            resource["preco_hora"] = r.json()['price']
            maquinas.append(resource)
            request = {
                "op": True,
                "maquina": resource["idmaquina"]
            }
            consultaJSON = {"request": json.dumps(request)}
            r = requests.get("http://localhost:"+str(10000+provedorId)+"/cliente", params=consultaJSON)
            print r.json()['mensagem']
        else:
            print r.json()['mensagem']+'\n'
    elif opcao == 2:
        if provedorId == -1:
            print 'No momento nao existem recursos a serem liberados.'
        else:
            print 'id\tpreco\tHD\tRAM\tVCPU'
            i = 0
            for maq in maquinas:
                print str(i) + '\t' + str(maq["preco_hora"]) + '\t' + str(maq["HD"]) + '\t' + str(maq["RAM"]) + '\t' + str(maq["qtd_vCPU"])
                i = i + 1
            idmaquina = int(raw_input("Qual maquina deseja liberar (digite o id): "))
            if idmaquina >= len(maquinas) or idmaquina < 0:
                print 'Valor nao esta dentro dos valores permitidos. Liberacao cancelada.'
            else:
                maquina = maquinas.pop(idmaquina)
                req = {
                    "maquina": maquina["idmaquina"],
                    "op": False
                }
                sendreq = {"request": json.dumps(req)}
                r = requests.get("http://localhost:"+str(10000+provedorId)+"/cliente", params=sendreq)
                print r.json()['mensagem']+'\n'
                if len(maquinas) == 0:
                    provedorId = -1

    else:
        print 'Opcao inexistente, escolha entre 1-Buscar recurso e 2-Liberar um recurso.'

    opcao = int(raw_input("Opcao: "))
