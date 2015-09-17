#
# This script creates the needed directories for OpenELIS.
#
BASE_DIR="/OpenELIS"
cd $BASE_DIR
if [ $? -ne 0 ]; then
    echo "Can't step into ase directory $BASE_DIR; exiting."
    exit 1
fi

echo "Please specify the JBOSS user (e.g. jboss): "
read JBOSS_USER
if [ -z $JBOSS_USER ]; then
    echo "JBOSS user is not defined; exiting"
    exit 1
fi

echo "Please specify the group for all your users (e.g. users): "
read USER_GROUP
if [ -z $USER_GROUP ]; then
    echo "User group is not defined; exiting"
    exit 1
fi

echo "Please specify the LIMS admin group (e.g. lisadmin): "
read LIMS_ADMIN_GROUP
if [ -z $LIMS_ADMIN_GROUP ]; then
    echo "LIMS admin group is not defined; exiting"
    exit 1
fi

mkdir upload
mkdir worksheets
mkdir wstemplate
mkdir inventory
mkdir billing
mkdir SDWIS
mkdir scan
mkdir scan/attachment
mkdir scan/system
mkdir instrument
mkdir docs
mkdir eorders
mkdir attachment
#
# created nested subdirectories to break up # files/directory
#
cd attachment
if [ $? -eq 0 ]; then
    for i in {0..9} ; do
        echo $i;
        mkdir $i;
        for j in {0..9} ; do
            mkdir $i/$j;
            for k in {0..9} ; do
                mkdir $i/$j/$k;
                for l in {0..9} ; do  
                    mkdir $i/$j/$k/$l;
                done;
            done;
        done;
    done;
fi

#
# set permission
#
cd ..
if [ $? -ne 0 ]; then
    echo "Can't switch to base directory; exiting"
    exit 1
fi

# directories open to both JBOSS application and windows clients

chmod 770 worksheets scan/attachment instrument
chown $JBOSS_USER:$USER_GROUP worksheets SDWIS scan/attachment instrument

# open to JBOSS and LIMS administrator group

chmod 770 upload wstemplate inventory billing SDWIS attachment scan scan/system docs eorders
chown $JBOSS_USER:$LIMS_ADMIN_GROUP upload wstemplate inventory billing SDWIS attachment scan scan/system docs eorders

