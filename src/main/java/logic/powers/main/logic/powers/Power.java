package logic.powers.main.logic.powers;

import java.util.Set;

public interface Power {
    public void resetUsage();
    public Set<String> CoordinatesGenerator(String AreaCenter);
}
