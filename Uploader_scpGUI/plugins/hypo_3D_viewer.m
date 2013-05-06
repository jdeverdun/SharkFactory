%@@main@@
%Calculate tesselation of the gland and show it
%@@file:respath@@select:surface/volume@@
%@@none@@
function hypo_3D_viewer( varargin )
%HYPO_3D_VIEWER Summary of this function goes here
%   Detailed explanation goes here
    pathres=varargin{1};
    non_gfp=csvread(strcat(pathres,'/coord_scaled_non_gfp.csv'));
    gfp=csvread(strcat(pathres,'/coord_scaled_gfp.csv'));
    all=[non_gfp;gfp];
    
end

