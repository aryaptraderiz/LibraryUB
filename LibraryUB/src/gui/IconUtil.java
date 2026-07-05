package gui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * IconUtil - Kumpulan icon vektor yang digambar manual menggunakan Java2D.
 *
 * Dibuat untuk menghindari ketergantungan pada font emoji (Segoe UI Emoji dkk)
 * yang seringkali tidak ter-install lengkap di sistem operasi tertentu, sehingga
 * tampil sebagai kotak putih (tofu). Dengan menggambar bentuk sendiri (garis,
 * lingkaran, kurva) menggunakan Graphics2D, tampilan ikon dijamin konsisten
 * di semua sistem tanpa bergantung pada font yang ter-install.
 *
 * OOP Concept: Encapsulation (logika rendering disembunyikan di balik method statis),
 *              digunakan secara polymorphic karena semua method mengembalikan
 *              tipe Icon (interface bawaan Swing).
 */
public class IconUtil {

    /**
     * Inner class generik yang menggambar bentuk apapun lewat lambda/Drawer.
     * OOP: Interface fungsional sederhana + implementasi Icon dari javax.swing
     */
    private interface Drawer {
        void draw(Graphics2D g, int size, Color color);
    }

    private static class VectorIcon implements Icon {
        private final int size;
        private final Color color;
        private final Drawer drawer;

        VectorIcon(int size, Color color, Drawer drawer) {
            this.size = size;
            this.color = color;
            this.drawer = drawer;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            drawer.draw(g2, size, color);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return size; }

        @Override
        public int getIconHeight() { return size; }
    }

    // ===================== ICON-ICON =====================

    /** Icon buku (untuk Login, Manajemen Buku) */
    public static Icon buku(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            float w = s * 0.7f, h = s * 0.55f;
            float x0 = (s - w) / 2, y0 = (s - h) / 2 + s * 0.05f;
            // sampul buku
            RoundRectangle2D cover = new RoundRectangle2D.Float(x0, y0, w, h, 3, 3);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.07f)));
            g.draw(cover);
            // garis tengah (lipatan buku)
            g.draw(new Line2D.Float(s / 2f, y0, s / 2f, y0 + h));
            // garis-garis halaman
            g.setStroke(new BasicStroke(Math.max(1f, s * 0.04f)));
            g.draw(new Line2D.Float(x0 + w * 0.15f, y0 + h * 0.3f, s / 2f - w * 0.08f, y0 + h * 0.25f));
            g.draw(new Line2D.Float(x0 + w * 0.15f, y0 + h * 0.6f, s / 2f - w * 0.08f, y0 + h * 0.55f));
        });
    }

    /** Icon rumah (Dashboard) */
    public static Icon rumah(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float pad = s * 0.18f;
            float baseY = s * 0.85f;
            float roofY = s * 0.15f;
            float midX = s / 2f;
            Path2D path = new Path2D.Float();
            path.moveTo(pad, s * 0.5f);
            path.lineTo(midX, roofY);
            path.lineTo(s - pad, s * 0.5f);
            g.draw(path);
            Rectangle2D wall = new Rectangle2D.Float(pad + s * 0.05f, s * 0.5f, s - 2 * (pad + s * 0.05f), baseY - s * 0.5f);
            g.draw(wall);
            Rectangle2D door = new Rectangle2D.Float(midX - s * 0.1f, baseY - s * 0.25f, s * 0.2f, s * 0.25f);
            g.draw(door);
        });
    }

    /** Icon panah keluar/masuk kotak (Peminjaman = keluar) */
    public static Icon panahKeluar(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // kotak bawah
            Rectangle2D box = new Rectangle2D.Float(s * 0.15f, s * 0.55f, s * 0.7f, s * 0.32f);
            g.draw(box);
            // panah ke atas
            g.draw(new Line2D.Float(s / 2f, s * 0.55f, s / 2f, s * 0.12f));
            Path2D arrow = new Path2D.Float();
            arrow.moveTo(s * 0.32f, s * 0.3f);
            arrow.lineTo(s / 2f, s * 0.1f);
            arrow.lineTo(s * 0.68f, s * 0.3f);
            g.draw(arrow);
        });
    }

    /** Icon panah masuk kotak (Pengembalian = masuk) */
    public static Icon panahMasuk(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Rectangle2D box = new Rectangle2D.Float(s * 0.15f, s * 0.55f, s * 0.7f, s * 0.32f);
            g.draw(box);
            g.draw(new Line2D.Float(s / 2f, s * 0.12f, s / 2f, s * 0.55f));
            Path2D arrow = new Path2D.Float();
            arrow.moveTo(s * 0.32f, s * 0.37f);
            arrow.lineTo(s / 2f, s * 0.57f);
            arrow.lineTo(s * 0.68f, s * 0.37f);
            g.draw(arrow);
        });
    }

    /** Icon orang tunggal (Avatar) */
    public static Icon orang(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float headR = s * 0.22f;
            g.draw(new Ellipse2D.Float(s / 2f - headR, s * 0.1f, headR * 2, headR * 2));
            Arc2D body = new Arc2D.Float(s * 0.15f, s * 0.5f, s * 0.7f, s * 0.6f, 0, 180, Arc2D.OPEN);
            g.draw(body);
        });
    }

    /** Icon dua orang (Manajemen Anggota) */
    public static Icon duaOrang(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.2f, s * 0.06f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float headR = s * 0.16f;
            // orang kiri (belakang, lebih kecil & transparan)
            g.draw(new Ellipse2D.Float(s * 0.12f, s * 0.18f, headR * 2, headR * 2));
            Arc2D bodyLeft = new Arc2D.Float(s * 0.02f, s * 0.5f, s * 0.45f, s * 0.45f, 0, 180, Arc2D.OPEN);
            g.draw(bodyLeft);
            // orang kanan (depan, lebih besar)
            g.draw(new Ellipse2D.Float(s * 0.48f, s * 0.12f, headR * 2.3f, headR * 2.3f));
            Arc2D bodyRight = new Arc2D.Float(s * 0.35f, s * 0.48f, s * 0.55f, s * 0.5f, 0, 180, Arc2D.OPEN);
            g.draw(bodyRight);
        });
    }

    /** Icon grafik batang (Laporan) */
    public static Icon grafik(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float baseY = s * 0.85f;
            g.fill(new Rectangle2D.Float(s * 0.18f, s * 0.55f, s * 0.16f, baseY - s * 0.55f));
            g.fill(new Rectangle2D.Float(s * 0.42f, s * 0.35f, s * 0.16f, baseY - s * 0.35f));
            g.fill(new Rectangle2D.Float(s * 0.66f, s * 0.15f, s * 0.16f, baseY - s * 0.15f));
            g.draw(new Line2D.Float(s * 0.08f, baseY, s * 0.92f, baseY));
        });
    }

    /** Icon pintu keluar / logout */
    public static Icon keluar(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Rectangle2D doorFrame = new Rectangle2D.Float(s * 0.18f, s * 0.15f, s * 0.4f, s * 0.7f);
            g.draw(doorFrame);
            g.draw(new Line2D.Float(s * 0.45f, s * 0.5f, s * 0.85f, s * 0.5f));
            Path2D arrow = new Path2D.Float();
            arrow.moveTo(s * 0.68f, s * 0.35f);
            arrow.lineTo(s * 0.85f, s * 0.5f);
            arrow.lineTo(s * 0.68f, s * 0.65f);
            g.draw(arrow);
        });
    }

    /** Icon gembok (Login) */
    public static Icon gembok(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // badan gembok
            RoundRectangle2D body = new RoundRectangle2D.Float(s * 0.2f, s * 0.45f, s * 0.6f, s * 0.42f, 4, 4);
            g.draw(body);
            // lengkungan atas
            Arc2D shackle = new Arc2D.Float(s * 0.3f, s * 0.08f, s * 0.4f, s * 0.5f, 0, 180, Arc2D.OPEN);
            g.draw(shackle);
            // lubang kunci
            g.fill(new Ellipse2D.Float(s / 2f - s * 0.05f, s * 0.6f, s * 0.1f, s * 0.1f));
        });
    }

    /** Icon plus (Tambah / Daftar / Pinjam) */
    public static Icon tambah(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.8f, s * 0.14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Line2D.Float(s / 2f, s * 0.15f, s / 2f, s * 0.85f));
            g.draw(new Line2D.Float(s * 0.15f, s / 2f, s * 0.85f, s / 2f));
        });
    }

    /** Icon pensil (Edit) */
    public static Icon edit(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D pencil = new Path2D.Float();
            pencil.moveTo(s * 0.2f, s * 0.8f);
            pencil.lineTo(s * 0.25f, s * 0.6f);
            pencil.lineTo(s * 0.65f, s * 0.2f);
            pencil.lineTo(s * 0.8f, s * 0.35f);
            pencil.lineTo(s * 0.4f, s * 0.75f);
            pencil.closePath();
            g.draw(pencil);
            g.draw(new Line2D.Float(s * 0.2f, s * 0.8f, s * 0.4f, s * 0.75f));
        });
    }

    /** Icon tempat sampah (Hapus) */
    public static Icon hapus(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Line2D.Float(s * 0.2f, s * 0.3f, s * 0.8f, s * 0.3f));
            g.draw(new RoundRectangle2D.Float(s * 0.28f, s * 0.3f, s * 0.44f, s * 0.55f, 3, 3));
            g.draw(new Line2D.Float(s * 0.38f, s * 0.18f, s * 0.62f, s * 0.18f));
            g.draw(new Line2D.Float(s * 0.38f, s * 0.18f, s * 0.32f, s * 0.3f));
            g.draw(new Line2D.Float(s * 0.62f, s * 0.18f, s * 0.68f, s * 0.3f));
            g.draw(new Line2D.Float(s / 2f, s * 0.42f, s / 2f, s * 0.72f));
        });
    }

    /** Icon kaca pembesar (Cari) */
    public static Icon cari(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.1f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Ellipse2D.Float(s * 0.15f, s * 0.15f, s * 0.5f, s * 0.5f));
            g.draw(new Line2D.Float(s * 0.58f, s * 0.58f, s * 0.85f, s * 0.85f));
        });
    }

    /** Icon silang (Batal/Tutup) */
    public static Icon silang(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.8f, s * 0.12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Line2D.Float(s * 0.2f, s * 0.2f, s * 0.8f, s * 0.8f));
            g.draw(new Line2D.Float(s * 0.8f, s * 0.2f, s * 0.2f, s * 0.8f));
        });
    }

    /** Icon centang (Sukses/Konfirmasi) */
    public static Icon centang(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.8f, s * 0.12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D check = new Path2D.Float();
            check.moveTo(s * 0.18f, s * 0.52f);
            check.lineTo(s * 0.42f, s * 0.75f);
            check.lineTo(s * 0.85f, s * 0.25f);
            g.draw(check);
        });
    }

    /** Icon panah kiri (Kembali) */
    public static Icon panahKiri(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.8f, s * 0.12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Line2D.Float(s * 0.78f, s / 2f, s * 0.2f, s / 2f));
            Path2D arrowHead = new Path2D.Float();
            arrowHead.moveTo(s * 0.42f, s * 0.28f);
            arrowHead.lineTo(s * 0.2f, s / 2f);
            arrowHead.lineTo(s * 0.42f, s * 0.72f);
            g.draw(arrowHead);
        });
    }

    /** Icon label/tag (Kategori) */
    public static Icon label(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D tag = new Path2D.Float();
            tag.moveTo(s * 0.15f, s * 0.2f);
            tag.lineTo(s * 0.55f, s * 0.2f);
            tag.lineTo(s * 0.85f, s * 0.5f);
            tag.lineTo(s * 0.55f, s * 0.8f);
            tag.lineTo(s * 0.15f, s * 0.8f);
            tag.closePath();
            g.draw(tag);
            g.fill(new Ellipse2D.Float(s * 0.3f, s * 0.42f, s * 0.13f, s * 0.13f));
        });
    }

    /** Icon clock (Waktu/Terlambat) */
    public static Icon jam(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Ellipse2D.Float(s * 0.12f, s * 0.12f, s * 0.76f, s * 0.76f));
            g.draw(new Line2D.Float(s / 2f, s / 2f, s / 2f, s * 0.28f));
            g.draw(new Line2D.Float(s / 2f, s / 2f, s * 0.68f, s * 0.58f));
        });
    }

    /** Icon uang/koin (Denda) */
    public static Icon uang(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(Math.max(1.5f, s * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Ellipse2D.Float(s * 0.12f, s * 0.12f, s * 0.76f, s * 0.76f));
            Font oldFont = g.getFont();
            g.setFont(oldFont.deriveFont(Font.BOLD, s * 0.42f));
            FontMetrics fm = g.getFontMetrics();
            String t = "Rp";
            int tw = fm.stringWidth(t);
            g.drawString(t, s / 2f - tw / 2f, s / 2f + fm.getAscent() * 0.35f);
        });
    }

    /** Icon kunci (Role/Akses) */
    public static Icon kunci(int size, Color color) {
        return gembok(size, color);
    }

    /** Lingkaran solid kecil (indikator status online) */
    public static Icon statusBulat(int size, Color color) {
        return new VectorIcon(size, color, (g, s, c) -> {
            g.setColor(c);
            g.fill(new Ellipse2D.Float(s * 0.15f, s * 0.15f, s * 0.7f, s * 0.7f));
        });
    }
}