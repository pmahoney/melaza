package org.polycrystal.melaza.instrument.clfilter;

import com.mentorgen.tools.profile.instrument.clfilter.ClassLoaderFilter;

/**
 * A ClassLoaderFilter that accepts all class loaders.
 * 
 * @since Dec 30, 2012
 * @author Patrick Mahoney <pat@polycrystal.org>
 *
 */
public final class PassThroughClassLoaderFilter implements ClassLoaderFilter {

    @Override
    public boolean canFilter() {
        return true;
    }

    @Override
    public boolean accept(ClassLoader loader) {
        return true;
    }

}
