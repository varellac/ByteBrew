# ☕ Cafeteria Byte & Brew

Trabalho prático da disciplina de Programação Orientada a Objetos.

O **Byte & Brew** é um sistema de vendas e programa de fidelidade modelado para uma cafeteria geek. O projeto tem como objetivo demonstrar a aplicação prática dos conceitos fundamentais de Orientação a Objetos em Java.

## 🚀 Conceitos Aplicados
* **Classes, Objetos e Associações:** Estrutura completa de carrinho de compras (Pedido, ItemPedido, Produto) associados a Clientes e Atendentes.
* **Herança:** Especialização de categorias no cardápio (`Comida` e `Bebida`) e níveis de fidelidade (`ClienteStandard` e `ClienteVIP`).
* **Polimorfismos:** 
  * Inclusão (tratar produtos genéricos no carrinho).
  * Sobrescrita (lógicas diferentes de cálculo de pontos XP).
  * Sobrecarga (múltiplas formas de adicionar um produto ao pedido).
  * Coerção (conversões implícitas/explícitas de tipos).
* **Tratamento de Exceções:** Criação de *Checked Exceptions* (`EstoqueInsuficienteException` e `PontosInsuficientesException`).

## ⚙️ Como executar

1. Clone o repositório.
2. Navegue até a pasta raiz do projeto.
3. Compile e execute a simulação (que inclui demonstração de CRUD e fluxo de caixa) com o comando:

```bash
javac src/br/edu/cafeteria/modelo/*.java src/br/edu/cafeteria/excecao/*.java src/br/edu/cafeteria/servico/*.java src/br/edu/cafeteria/app/*.java
java -cp src br.edu.cafeteria.app.Main
```