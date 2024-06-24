import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

interface ITaxas {
    double calculaTaxas();
}

abstract class Operacao {
    private Date data;
    private char tipo;
    private float valor;

    public Date getData() {
        return this.data;
    }

    public char getTipo() {
        return this.tipo;
    }

    public void setTipo(char tipo) {
        if (tipo == 'd' || tipo == 's') {
            this.tipo = tipo;
        }
    }

    public float getValor() {
        return this.valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public Operacao(char tipo, float valor) {
        this.tipo = tipo;
        this.valor = valor;
        data = new Date();
    }

    void extrato() {
        System.out.println(getData() + " " + getTipo() + " " + getValor());
    }

    @Override
    public String toString() {
        return "Operacao{" +
                "data=" + data +
                ", tipo=" + tipo +
                ", valor=" + valor +
                '}';
    }
}

class Saca extends Operacao {
    public Saca(float valor) {
        super('S', valor);
    }
}

class Deposita extends Operacao {
    public Deposita(float valor) {
        super('D', valor);
    }
}

abstract class Cliente {
    private String nome;
    String endereco;
    Date dataCliente;

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public abstract boolean autenticar(String chave);
}

class ClientePessoaFisica extends Cliente {
    String CPF;
    int idade;
    char sexo;

    @Override
    public String toString() {
        return "ClientePessoaFisica{" +
                "nome='" + getNome() + '\'' +
                ", CPF='" + CPF + '\'' +
                ", endereco='" + endereco + '\'' +
                ", idade=" + idade +
                ", sexo=" + sexo +
                '}';
    }

    @Override
    public boolean autenticar(String chave) {
        return this.CPF.equals(chave);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientePessoaFisica that = (ClientePessoaFisica) o;
        return Objects.equals(CPF, that.CPF);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CPF);
    }
}

class ClientePessoaJuridica extends Cliente {
    String CNPJ;
    int numFuncionarios;
    String setor;

    @Override
    public String toString() {
        return "ClientePessoaJuridica{" +
                "nome='" + getNome() + '\'' +
                ", CNPJ='" + CNPJ + '\'' +
                ", endereco='" + endereco + '\'' +
                ", numFuncionarios=" + numFuncionarios +
                ", setor='" + setor + '\'' +
                '}';
    }

    @Override
    public boolean autenticar(String chave) {
        return this.CNPJ.equals(chave);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientePessoaJuridica that = (ClientePessoaJuridica) o;
        return Objects.equals(CNPJ, that.CNPJ);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CNPJ);
    }
}

abstract class Conta {
    Operacao[] operacoes = new Operacao[999];
    Cliente cliente;
    private int numero;
    private float saldo_atual = 0;
    protected float limite;
    int x = 0;

    public int getNumero() {
        return this.numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public float getSaldo() {
        return this.saldo_atual;
    }

    public float getLimite() {
        return this.limite;
    }

    public abstract void setLimite(float limite);

    void saca(float quantidade) {
        if (saldo_atual >= quantidade) {
            float nv_saldo = this.saldo_atual - quantidade;
            saldo_atual = nv_saldo;
            operacoes[x] = new Saca(quantidade);
            x++;
            System.out.println("Saque realizado com sucesso, no valor de BRL " + quantidade);
        } else {
            System.out.println("Saldo Insuficiente");
        }
    }

    void depositar(float quantidade) {
        float nv_saldo = this.saldo_atual + quantidade;
        if (nv_saldo > this.limite) {
            System.out.println("Limite estourado!!!");
        } else {
            saldo_atual = nv_saldo;
            operacoes[x] = new Deposita(quantidade);
            x++;
            System.out.println("Deposito realizado com sucesso, no valor de BRL " + quantidade);
        }
    }

    void imprimirExt() {
        System.out.println(" ");
        for (int i = 0; i < x; i++) {
            operacoes[i].extrato();
        }
        System.out.println(" ");
    }

    void imprimirExtratoTaxas() {
        double totalTaxas = 0.0;
        System.out.println("Extrato de Taxas:");
        for (Operacao operacao : operacoes) {
            if (operacao instanceof ITaxas) {
                ITaxas taxa = (ITaxas) operacao;
                double valorTaxa = taxa.calculaTaxas();
                totalTaxas += valorTaxa;
                System.out.println("Taxa: " + valorTaxa);
            }
        }
        System.out.println("Total de Taxas: " + totalTaxas);
    }

    @Override
    public String toString() {
        return "Conta{" +
                "numero=" + numero +
                ", saldo_atual=" + saldo_atual +
                ", limite=" + limite +
                ", cliente=" + cliente +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return numero == conta.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }
}

class ContaCorrente extends Conta implements ITaxas {
    @Override
    public void setLimite(float limite) {
        if (limite >= -100) {
            this.limite = limite;
        } else {
            this.limite = -100;
        }
    }

    @Override
    public double calculaTaxas() {
        if (cliente instanceof ClientePessoaFisica) {
            return 10.0;
        } else if (cliente instanceof ClientePessoaJuridica) {
            return 20.0;
        }
        return 0.0; // Caso não seja nenhum dos tipos acima
    }
}

class ContaPoupanca extends Conta implements ITaxas {
    @Override
    public void setLimite(float limite) {
        if (limite >= 100 && limite <= 1000) {
            this.limite = limite;
        } else if (limite < 100) {
            this.limite = 100;
        } else {
            this.limite = 1000;
        }
    }

    @Override
    public double calculaTaxas() {
        return 0.0; // Não cobra taxa
    }
}

class ContaUniversitaria extends Conta implements ITaxas {
    @Override
    public void setLimite(float limite) {
        if (limite >= 0 && limite <= 500) {
            this.limite = limite;
        } else if (limite < 0) {
            this.limite = 0;
        } else {
            this.limite = 500;
        }
    }

    @Override
    public double calculaTaxas() {
        return 0.0; // Não cobra taxa
    }
}

class OperacaoSaque extends Operacao implements ITaxas {
    public OperacaoSaque(float valor) {
        super('S', valor);
    }

    @Override
    public double calculaTaxas() {
        return 0.05; // 5 centavos por saque
    }
}

class BMG {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Conta conta = null;
        Cliente cliente = null;
        ClientePessoaFisica pf = new ClientePessoaFisica();
        ClientePessoaJuridica pj = new ClientePessoaJuridica();

        int x;
        int y;
        int z = 0;
        float quantidade;

        System.out.println("-----BANCO BMG-----");
        System.out.println("Vamos criar sua conta!");
        System.out.println("Identifique-se:");
        System.out.println("[1] - Pessoa Fisica");
        System.out.println("[2] - Pessoa Juridica");
        System.out.print("Insira um valor: ");
        y = sc.nextInt();

        switch (y) {
            case 1:
                z = 1;
                System.out.print("Insira o numero da conta: ");
                conta = new ContaCorrente();
                conta.setNumero(sc.nextInt());
                System.out.print("Insira seu limite: ");
                conta.setLimite(sc.nextFloat());
                sc.nextLine();
                System.out.print("Insira seu nome: ");
                pf.setNome(sc.nextLine());
                System.out.print("Insira seu CPF: ");
                pf.CPF = sc.nextLine();
                System.out.print("Insira seu endereco: ");
                pf.endereco = sc.nextLine();
                System.out.print("Insira sua idade: ");
                pf.idade = sc.nextInt();
                System.out.print("Insira seu sexo: ");
                pf.sexo = sc.next().charAt(0);
                System.out.println(" ");
                sc.nextLine();
                conta.cliente = pf;
                cliente = pf;
                break;

            case 2:
                z = 2;
                System.out.print("Insira o numero da conta: ");
                conta = new ContaPoupanca();
                conta.setNumero(sc.nextInt());
                System.out.print("Insira seu limite: ");
                conta.setLimite(sc.nextFloat());
                sc.nextLine();
                System.out.print("Insira seu nome: ");
                pj.setNome(sc.nextLine());
                System.out.print("Insira seu CNPJ: ");
                pj.CNPJ = sc.nextLine();
                System.out.print("Insira seu endereco: ");
                pj.endereco = sc.nextLine();
                System.out.print("Insira seu setor: ");
                pj.setor = sc.nextLine();
                System.out.print("Insira sua quantidade de funcionarios: ");
                pj.numFuncionarios = sc.nextInt();
                System.out.println(" ");
                sc.nextLine();
                conta.cliente = pj;
                cliente = pj;
                break;

            default:
                System.out.println("Valor Invalido, tente novamente.");
                System.exit(0);
                break;
        }

        do {
            System.out.println("-----BANCO BMG-----");
            System.out.println(" ");
            System.out.println("Saldo Atual = " + conta.getSaldo());
            System.out.println(" ");
            System.out.println("[1] - Imprimir dados da sua conta");
            System.out.println("[2] - Sacar");
            System.out.println("[3] - Depositar");
            System.out.println("[4] - Imprimir extrato");
            System.out.println("[0] - Sair");

            System.out.print("Insira um valor: ");
            x = sc.nextInt();
            sc.nextLine();
            System.out.println("");

            switch (x) {
                case 1:
                    System.out.println(conta);
                    System.out.println(cliente);
                    break;

                case 2:
                    quantidade = 0;
                    System.out.print("Insira a quantidade que deseja sacar: ");
                    quantidade = sc.nextFloat();
                    conta.saca(quantidade);
                    break;

                case 3:
                    quantidade = 0;
                    System.out.print("Insira a quantidade que deseja depositar: ");
                    quantidade = sc.nextFloat();
                    conta.depositar(quantidade);
                    break;

                case 4:
                    conta.imprimirExt();
                    conta.imprimirExtratoTaxas(); // Imprime extrato de taxas
                    break;

                case 0:
                    System.out.println("Obrigado por usar o Banco BMG!");
                    break;

                default:
                    System.out.println("Valor Invalido, tente novamente.");
                    break;
            }

            System.out.println("");

        } while (x != 0);
        sc.close();
    }
}
