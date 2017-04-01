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
public class UserStat
{

    private String ip, mac, name;
    private long totalPac, totalSz, curPac, curSz;
    int id;
    private boolean active;

    public UserStat(String ip, String mac, String name, long totalPac, long totalSz, long curPac, long curSz, int id, boolean active)
    {
        this.ip = ip;
        this.mac = mac;
        this.name = name;
        this.totalPac = totalPac;
        this.totalSz = totalSz;
        this.curPac = curPac;
        this.curSz = curSz;
        this.id = id;
        this.active = active;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMac()
    {
        return mac;
    }

    public void setMac(String mac)
    {
        this.mac = mac;
    }

    public long getTotalPac()
    {
        return totalPac;
    }

    public void setTotalPac(long totalPac)
    {
        this.totalPac = totalPac;
    }

    public long getTotalSz()
    {
        return totalSz;
    }

    public void setTotalSz(long totalSz)
    {
        this.totalSz = totalSz;
    }

    public long getCurPac()
    {
        return curPac;
    }

    public void setCurPac(long curPac)
    {
        this.curPac = curPac;
    }

    public long getCurSz()
    {
        return curSz;
    }

    public void setCurSz(long curSz)
    {
        this.curSz = curSz;
    }

    public String getSzCurSz()
    {
        return TpLink.toSize(curSz);
    }

    public String getSzTotalSz()
    {
        return TpLink.toSize(totalSz);
    }

}
