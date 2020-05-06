package dds.monedero.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public void poner(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }

    if (cantidadDeDepositosDeHoy() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    Deposito deposito = new Deposito(LocalDate.now(), cuanto);
    this.saldo = deposito.calcularValor(this.saldo);
    this.movimientos.add(deposito);
  }

  public void sacar(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    if (cuanto > limiteRestanteDeExtraccion()) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limiteRestanteDeExtraccion());
    }

    Extraccion extraccion = new Extraccion(LocalDate.now(), cuanto);
    this.saldo = extraccion.calcularValor(this.saldo);
    this.movimientos.add(extraccion);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.isExtraccion() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public long cantidadDeDepositosDeHoy() {
    return this.movimientos.stream().filter(movimiento -> movimiento.isDeposito() && movimiento.esDeLaFecha(LocalDate.now())).count();
  }

  public double limiteRestanteDeExtraccion() {
    return 1000 - getMontoExtraidoA(LocalDate.now());
  }

}
