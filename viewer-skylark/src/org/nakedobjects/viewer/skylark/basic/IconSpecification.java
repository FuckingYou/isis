package org.nakedobjects.viewer.skylark.basic;

import org.nakedobjects.object.Naked;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.viewer.skylark.Click;
import org.nakedobjects.viewer.skylark.Content;
import org.nakedobjects.viewer.skylark.Location;
import org.nakedobjects.viewer.skylark.MenuOption;
import org.nakedobjects.viewer.skylark.MenuOptionSet;
import org.nakedobjects.viewer.skylark.ObjectContent;
import org.nakedobjects.viewer.skylark.Style;
import org.nakedobjects.viewer.skylark.View;
import org.nakedobjects.viewer.skylark.ViewAreaType;
import org.nakedobjects.viewer.skylark.ViewAxis;
import org.nakedobjects.viewer.skylark.ViewSpecification;
import org.nakedobjects.viewer.skylark.Workspace;
import org.nakedobjects.viewer.skylark.core.AbstractViewDecorator;


public class IconSpecification implements ViewSpecification {
	private boolean isSubView;
	private boolean isReplaceable;

	public IconSpecification() {
	    this(true, true);
	}
	
	IconSpecification(boolean isSubView, boolean isReplaceable) {
		this.isSubView = isSubView;
		this.isReplaceable = isReplaceable;
	}
	
	public boolean canDisplay(Naked object) {
		return object instanceof NakedObject && object != null;
	}
	
	public View createView(Content content, ViewAxis axis) {
		return new SimpleBorder(1, new CloseIcon(new IconView(content, this, axis, Style.NORMAL)));
    }

	public String getName() {
        return "Icon";
    }

	public boolean isSubView() {
		return isSubView;
	}
	
	public boolean isReplaceable() {
		return isReplaceable;
	}

	public View decorateSubview(View subview) {
		return subview;
	}

	public boolean isOpen() {
		return false;
	}
}

class CloseIcon extends AbstractViewDecorator {
    protected CloseIcon(View wrappedView) {
        super(wrappedView);
    }
    
    public void secondClick(Click click) {
        if(click.getViewAreaType() == ViewAreaType.VIEW) {
            openIcon();
        } else {
            super.secondClick(click);
        }
    }
    
    private void openIcon() {
        closeIcon();
        getWorkspace().addOpenViewFor(((ObjectContent) getContent()).getObject(), getLocation());
    }

    private void closeIcon() {
        getWorkspace().removeView(getView());
    }

    public void menuOptions(MenuOptionSet menuOptions) {
        super.menuOptions(menuOptions);
        
        menuOptions.add(MenuOptionSet.VIEW, new MenuOption("Remove icon from workspace") {
            public void execute(Workspace workspace, View view, Location at) {
                closeIcon();
            }});

        menuOptions.add(MenuOptionSet.VIEW, new MenuOption("Open object") {
            public void execute(Workspace workspace, View view, Location at) {
                openIcon();
            }});
    }
}


/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2004  Naked Objects Group Ltd

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The authors can be contacted via www.nakedobjects.org (the
registered address of Naked Objects Group is Kingsway House, 123 Goldworth
Road, Woking GU21 1NR, UK).
*/
