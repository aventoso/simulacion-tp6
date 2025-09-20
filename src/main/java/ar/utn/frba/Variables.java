package ar.utn.frba;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class Variables {

    private Estado estado;
    private Control control;

    LocalDateTime t = LocalDateTime.of(2025, 1, 1, 9, 0, 0);
    LocalDateTime tpll = LocalDateTime.of(2025, 1, 1, 9, 0, 0);

    int longitudPromedioCola = 0;
    Integer[] tiempoPromedioEsperaCola = new Integer[3];
    Integer[] cantidadTicketsResueltos = new Integer[2];

    private int i;
    private int j;
    private int k;

    public Variables(Estado estado, Control control) {
        this.estado = estado;
        this.control = control;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class Control {
        private int nj;
        private int nss;
        private int ns;
    }

    @Getter
    @Setter
    public static class Estado {

        private int nj;
        private int nss;
        private int ns;


        private int cantidadCriticos = 0;
        private int cantidadAltos = 0;
        private int cantidadMedios = 0;
        private int cantidadBajos = 0;
        private String[] seniorAtendiendo;
        private String[] sseniorAtendiendo;
        private String[] juniorAtendiendo;
        private Integer bajosEncolados = 0;
        private LocalTime[] tpsJr;
        private LocalTime[] tpsSsr;
        private LocalTime[] tpsSr;

        public Estado(int nj, int nss, int ns) {
            this.ns = ns;
            this.nss = nss;
            this.nj = nj;
            this.tpsJr = new LocalTime[Math.max(nj, 0)];
            this.tpsSsr = new LocalTime[Math.max(nss, 0)];
            this.tpsSr = new LocalTime[Math.max(ns, 0)];
            this.seniorAtendiendo = new String[Math.max(ns, 0)];
            this.sseniorAtendiendo = new String[Math.max(nss, 0)];
            this.juniorAtendiendo = new String[Math.max(nj, 0)];
        }

        public void cPlus() {
            cantidadCriticos++;
        }

        public void cMinus() {
            cantidadCriticos--;
        }

        public void aPlus() {
            cantidadAltos++;
        }

        public void aMinus() {
            cantidadAltos--;
        }

        public void bPlus() {
            cantidadBajos++;
        }

        public void bMinus() {
            cantidadBajos--;
        }

        public void mPlus() {
            cantidadMedios++;
        }

        public void mMinus() {
            cantidadMedios--;
        }

        public void bePlus() {
            bajosEncolados++;
        }


    }

}


