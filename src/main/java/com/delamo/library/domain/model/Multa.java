package com.delamo.library.domain.model;

import java.math.BigDecimal;

public class Multa {

    private final String id;
    private final String socioId;
    private final String prestamoId;
    private final Double importe;
    private boolean pagada;

    public Multa(String id, String socioId, String prestamoId, Double importe) {
        this.id = id;
        this.socioId = socioId;
        this.prestamoId = prestamoId;
        this.importe = importe;
    }

    public String getId() {
        return id;
    }
    public String getSocioId() {
        return socioId;
    }
    public String getPrestamoId() {
        return prestamoId;
    }
    public Double getImporte() {
        return importe;
    }
    public boolean estaPagada() {
        return pagada;
    }

    public void marcarPagada() {
        pagada = true;
    }
}
