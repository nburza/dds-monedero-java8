package dds.monedero.model;

import java.time.LocalDate;

public class Deposito extends Movimiento{

    public Deposito(LocalDate fecha, double monto) {
        super(fecha, monto);
    }

    public boolean isDeposito() {
        return true;
    }

    public boolean isExtraccion() {
        return false;
    }

    public double calcularValor(double saldo) {
        return saldo + getMonto();
    }
}
