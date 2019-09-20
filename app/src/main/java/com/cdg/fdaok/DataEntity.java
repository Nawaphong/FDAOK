package com.cdg.fdaok;

public class DataEntity {
    private String cncnm; //สถานะ
    private String typepro; //ประเภทผลิตภัณฑ์
    private String lcnno; //ใบสำคัญ/เลขที่อนุญาต
    private String productha; //ชื่อผลิตภัณฑ์ (TH)
    private String produceng; //ชื่อผลิตภัณฑ์ (EN)
    private String licen; //ชื่อผู้รับอนุญาต
    private String thanm; //สถานที่ผลิต
    private String Addr; //ที่อยู่สถานที่ผลิต
    private String NewCode;//Newcode


    public String getLcnno() {
        return lcnno;
    }

    public void setLcnno(String lcnno) {
        this.lcnno = lcnno;
    }

    public String getProductha() {
        return productha;
    }

    public void setProductha(String productha) {
        this.productha = productha;
    }

    public String getProduceng() {
        return produceng;
    }

    public void setProduceng(String produceng) {
        this.produceng = produceng;
    }

    public String getLicen() {
        return licen;
    }

    public void setLicen(String licen) {
        this.licen = licen;
    }

    public String getCncnm() {
        return cncnm;
    }

    public void setCncnm(String cncnm) {
        this.cncnm = cncnm;
    }

    public String getTypepro() {
        return typepro;
    }

    public void setTypepro(String typepro) {
        this.typepro = typepro;
    }

    public String getThanm() {
        return thanm;
    }

    public void setThanm(String thanm) {
        this.thanm = thanm;
    }

    public String getAddr() {
        return Addr;
    }

    public void setAddr(String addr) {
        Addr = addr;
    }

    public String getNewCode() {
        return NewCode;
    }

    public void setNewCode(String newCode) {
        NewCode = newCode;
    }
}
