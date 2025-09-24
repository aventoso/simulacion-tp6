package ar.utn.frba;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class Variables {

    private Estado estado;
    private Control control;
    private Resultado resultado;

    private LocalDateTime t = LocalDateTime.of(2025, 1, 1, 9, 0, 0);
    private LocalDateTime tpll = LocalDateTime.of(2025, 1, 1, 9, 0, 0);



    private Integer cantidadTotalTickets = 0;

    private int i;
    private int j;
    private int k;

    public Variables(Estado estado, Control control, Resultado resultado) {
        this.estado = estado;
        this.control = control;
        this.resultado = resultado;
    }

    @Getter
    @Setter
    public static class Resultado {
        private Double stsB = 0.0;
        private Double stsM = 0.0;
        private Double stsA = 0.0;
        private Double stsC = 0.0;

        private Double staB = 0.0;
        private Double staM = 0.0;
        private Double staA = 0.0;
        private Double staC = 0.0;

        private Integer crJr = 0;
        private Integer crSsr = 0;
        private Integer crSr = 0;

        private Double cttb = 0.0;
        private Double cttm = 0.0;
        private Double ctta = 0.0;
        private Double cttc = 0.0;
        private Double ctt = 0.0;




        private Double[] distribucionTicketsPorCriticidad = new Double[3];
        private Integer[] tiempoPromedioEsperaCola = new Integer[3];
        private Integer[] cantidadTicketsResueltos = new Integer[2];

        public void addStaB(Double ta){
            long segundos = Math.round(ta * 60.0);
            staB = staB + segundos;
        }

        public void addStaM(Double ta){
            long segundos = Math.round(ta * 60.0);
            staM = staM + segundos;
        }

        public void addStaA(Double ta){
            long segundos = Math.round(ta * 60.0);
            staA = staA + segundos;
        }

        public void addStaC(Double ta){
            long segundos = Math.round(ta * 60.0);
            staC = staC + segundos;
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
        private LocalDateTime[] tpsJr;
        private LocalDateTime[] tpsSsr;
        private LocalDateTime[] tpsSr;

        public Estado(int nj, int nss, int ns) {
            this.ns = ns;
            this.nss = nss;
            this.nj = nj;
            this.tpsJr = new LocalDateTime[Math.max(nj, 0)];
            this.tpsSsr = new LocalDateTime[Math.max(nss, 0)];
            this.tpsSr = new LocalDateTime[Math.max(ns, 0)];
        }

        public void cPlus() {
            criticosEncolados++;
        }

        public void cMinus() {
            criticosEncolados--;
        }

        public void aPlus() {
            altosEncolados++;
        }

        public void aMinus() {
            altosEncolados--;
        }

        public void bPlus() {
            bajosEncolados++;
        }

        public void bMinus() {
            bajosEncolados--;
        }

        public void mPlus() {
            mediosEncolados++;
        }

        public void mMinus() {
            mediosEncolados--;
        }


    }

}


