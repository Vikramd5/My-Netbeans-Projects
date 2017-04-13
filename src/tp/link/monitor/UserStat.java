/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.link.monitor;

/**
 *
 * @author Vikram
 */
public class UserStat {

    final private String ip, mac, name, vendor;
    final private long totalPac, totalSz, curPac, curSz;
    final int id;
    final private boolean active;

    public UserStat(String ip, String mac, String name, long totalPac, long totalSz, long curPac, long curSz, int id, boolean active) {
        this.ip = ip;
        this.mac = mac;
        this.name = name;
        this.totalPac = totalPac;
        this.totalSz = totalSz;
        this.curPac = curPac;
        this.curSz = curSz;
        this.id = id;
        this.active = active;
        vendor = TpLink.lookupMAC(mac);
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }

    public long getTotalPac() {
        return totalPac;
    }

    public long getTotalSz() {
        return totalSz;
    }

    public long getCurPac() {
        return curPac;
    }

    public long getCurSz() {
        return curSz;
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public String getSzCurSz() {
        return TpLink.toSize(curSz);
    }

    public String getSzTotalSz() {
        return TpLink.toSize(totalSz);
    }

    public String getVendor() {
        return vendor;
    }

}
