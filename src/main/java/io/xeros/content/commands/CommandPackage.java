package io.xeros.content.commands;

import io.xeros.model.entity.player.Right;

public class CommandPackage {
    private final String packagePath;
    private final Right right;

    public CommandPackage(final String packagePath, final Right right) {
        this.packagePath = packagePath;
        this.right = right;
    }

    public String getPackagePath() {
        return this.packagePath;
    }

    public Right getRight() {
        return this.right;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CommandPackage)) return false;
        final CommandPackage other = (CommandPackage) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$packagePath = this.getPackagePath();
        final Object other$packagePath = other.getPackagePath();
        if (this$packagePath == null ? other$packagePath != null : !this$packagePath.equals(other$packagePath)) return false;
        final Object this$right = this.getRight();
        final Object other$right = other.getRight();
        if (this$right == null ? other$right != null : !this$right.equals(other$right)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CommandPackage;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $packagePath = this.getPackagePath();
        result = result * PRIME + ($packagePath == null ? 43 : $packagePath.hashCode());
        final Object $right = this.getRight();
        result = result * PRIME + ($right == null ? 43 : $right.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "CommandPackage(packagePath=" + this.getPackagePath() + ", right=" + this.getRight() + ")";
    }
}
