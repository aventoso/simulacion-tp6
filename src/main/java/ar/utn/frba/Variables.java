package ar.utn.frba;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Variables {

    private Estado estado;
    private Control control;
    private Resultado resultado;

    private LocalDateTime t = LocalDateTime.of(2025, 1, 1, 9, 0, 0);
    private LocalDateTime tpll = LocalDateTime.of(2025, 1, 1, 9, 0, 0);


    private Integer i;
    private Integer j;
    private Integer k;

    public Variables(Estado estado, Control control, Resultado resultado) {
        this.estado = estado;
        this.control = control;
        this.resultado = resultado;
    }

    @Getter
    @Setter
    public static class Resultado {
        /*STSx -> acumulador tiempos de permanencia en sistema*/
        private Double stsB = 0.0; // -> minutos
        private Double stsM = 0.0;
        private Double stsA = 0.0;
        private Double stsC = 0.0;

        /*STA<x -> acumulador tiempos de atencion en sistema*/
        private Double staB = 0.0; // -> minutos
        private Double staM = 0.0;
        private Double staA = 0.0;
        private Double staC = 0.0;

        /*CRx -> cantidad de tickets resueltos por senority*/
        private Integer crJr = 0;
        private Integer crSsr = 0;
        private Integer crSr = 0;

        /*CTTx -> cantidad total de tickets por senority*/
        private Double cttb = 0.0;
        private Double cttm = 0.0;
        private Double ctta = 0.0;
        private Double cttc = 0.0;
        private Double ctt = 0.0;


        public Map<String, String> calculateTPEC() {

            Map<String, String> resultado = new HashMap<>();

            DecimalFormat df = new DecimalFormat("#0.00");

            resultado.put("Tiempo promedio de espera en cola de BAJOS", df.format((stsB - staB) / cttb));
            resultado.put("Tiempo promedio de espera en cola de MEDIOS", df.format((stsM - staM) / cttm));
            resultado.put("Tiempo promedio de espera en cola de ALTOS", df.format((stsA - staA) / ctta));
            resultado.put("Tiempo promedio de espera en cola de CRITICOS", df.format((stsC - staC) / cttc));

            return resultado;
        }

        public Map<String, String> calculateCTR() {

            Map<String, String> resultado = new HashMap<>();

            resultado.put("Porcentaje de tickets resueltos por junior", getPorcentajeFormat((crJr / ctt) * 100, 5));
            resultado.put("Porcentaje de tickets resueltos por semi senior", getPorcentajeFormat((crSsr / ctt) * 100, 5));
            resultado.put("Porcentaje de tickets resueltos por senior", getPorcentajeFormat((crSr / ctt) * 100, 5));
            resultado.put("Porcentaje de tickets pendientes de resolucion", getPorcentajeFormat(((ctt - crJr - crSsr - crSr) / ctt) * 100, 5));

            return resultado;
        }

        public Map<String, String> calculateDTC() {

            Map<String, String> resultado = new HashMap<>();

            resultado.put("Porcentaje de tickets BAJOS", getPorcentajeFormat((cttb / ctt) * 100));
            resultado.put("Porcentaje de tickets MEDIOS", getPorcentajeFormat((cttm / ctt) * 100));
            resultado.put("Porcentaje de tickets ALTOS", getPorcentajeFormat((ctta / ctt) * 100));
            resultado.put("Porcentaje de tickets CRITICOS", getPorcentajeFormat((cttc / ctt) * 100));

            return resultado;
        }

        private String getPorcentajeFormat(Double valor) {
            return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_EVEN).toString() + "%";
        }

        private String getPorcentajeFormat(Double valor, int scale) {
            return BigDecimal.valueOf(valor).setScale(scale, RoundingMode.HALF_EVEN).toString() + "%";
        }


        public void addStaB(Double ta) {
            staB = staB + ta;
        }

        public void addStaM(Double ta) {
            staM = staM + ta;
        }

        public void addStaA(Double ta) {
            staA = staA + ta;
        }

        public void addStaC(Double ta) {
            staC = staC + ta;
        }

        public void addStsB(LocalDateTime t2, LocalDateTime eventoFuturo2, Integer ctb) {
            long diferenciaMinutos = Math.abs(Duration.between(t2,eventoFuturo2).toMinutes());
            this.stsB = this.stsB + (diferenciaMinutos * ctb);
        }

        public void addStsM(LocalDateTime t2, LocalDateTime eventoFuturo2, Integer ctm) {
            long diferenciaMinutos = Math.abs(Duration.between(t2,eventoFuturo2).toMinutes());
            this.stsM = this.stsM + (diferenciaMinutos * ctm);
        }

        public void addStsA(LocalDateTime t2, LocalDateTime eventoFuturo2, Integer cta) {
            long diferenciaMinutos = Math.abs(Duration.between(t2,eventoFuturo2).toMinutes());
            this.stsA = this.stsA + (diferenciaMinutos * cta);
        }

        public void addStsC(LocalDateTime t2, LocalDateTime eventoFuturo2, Integer ctc) {
            long diferenciaMinutos = Duration.between(t2,eventoFuturo2).toMinutes();
            this.stsC = this.stsC + (diferenciaMinutos * ctc);
        }

    }


    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class Control {
        private int nj;
        private int nss;
        private int ns;
    }

    @Getter
    @Setter
    @ToString
    public static class Estado {

        private int nj;
        private int nss;
        private int ns;


        private Integer criticosEncolados = 0;
        private Integer altosEncolados = 0;
        private Integer mediosEncolados = 0;
        private Integer bajosEncolados = 0;
        private Integer ctc = 0;
        private Integer cta = 0;
        private Integer ctm = 0;
        private Integer ctb = 0;
        private LocalDateTime[] tpsJr;
        private LocalDateTime[] tpsSsr;
        private LocalDateTime[] tpsSr;
        private String[] juniorAtendiendo;
        private String[] semiSeniorAtendiendo;
        private String[] seniorAtendiendo;

        public Estado(int nj, int nss, int ns) {
            this.ns = ns;
            this.nss = nss;
            this.nj = nj;
            this.tpsJr = new LocalDateTime[Math.max(nj, 0)];
            this.tpsSsr = new LocalDateTime[Math.max(nss, 0)];
            this.tpsSr = new LocalDateTime[Math.max(ns, 0)];
            this.juniorAtendiendo = new String[Math.max(nj, 0)];
            this.semiSeniorAtendiendo = new String[Math.max(nss, 0)];
            this.seniorAtendiendo = new String[Math.max(ns, 0)];
        }

        public void criticosEncoladosPlus() {
            criticosEncolados++;
        }

        public void criticosEncoladosMinus() {
            criticosEncolados--;
        }

        public void altosEncoladosPlus() {
            altosEncolados++;
        }

        public void altosEncoladosMinus() {
            altosEncolados--;
        }

        public void bajosEncoladosPlus() {
            bajosEncolados++;
        }

        public void bajosEncoladosMinus() {
            bajosEncolados--;
        }

        public void mediosEncoladosPlus() {
            mediosEncolados++;
        }

        public void mediosEncoladosMinus() {
            mediosEncolados--;
        }


    }

}


